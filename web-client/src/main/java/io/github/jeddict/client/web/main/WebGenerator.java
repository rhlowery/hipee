/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.client.web.main;

import io.github.jeddict.client.web.main.domain.ApplicationSourceFilter;
import io.github.jeddict.client.web.main.domain.BaseApplicationConfig;
import io.github.jeddict.client.web.main.domain.BaseEntity;
import io.github.jeddict.client.web.main.domain.EntityConfig;
import io.github.jeddict.client.web.main.domain.NeedleFile;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.BOLD;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import static io.github.jeddict.jcode.console.Console.UNDERLINE;
import io.github.jeddict.jcode.parser.ejs.EJSParser;
import static io.github.jeddict.jcode.parser.ejs.EJSUtil.copyDynamicResource;
import static io.github.jeddict.jcode.parser.ejs.EJSUtil.getResource;
import static io.github.jeddict.jcode.parser.ejs.EJSUtil.insertNeedle;
import io.github.jeddict.jcode.util.BuildManager;
import static io.github.jeddict.jcode.util.FileUtil.loadResource;
import static io.github.jeddict.jcode.util.FileUtil.readString;
import io.github.jeddict.jcode.util.StringHelper;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.extend.JavaClass;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import javax.script.ScriptException;
import org.openide.util.Exceptions;

public abstract class WebGenerator extends BaseWebGenerator {

    protected ApplicationSourceFilter fileFilter;
    protected final Function<String, String> PATH_RESOLVER = template -> fileFilter.isEnable(template)? template : null;
    
    @Override
    protected void generateClientSideComponent() {
        try {
            BaseApplicationConfig applicationConfig = getAppConfig();
            fileFilter = getApplicationSourceFilter(applicationConfig);

            if (appConfigData.isMonolith() || appConfigData.isGateway()) {
                if (appConfigData.isCompleteApplication()) {
                    EJSParser parser = new EJSParser();
                    parser.addContext(applicationConfig);
                    parser.eval(readString(getClass().getResourceAsStream(getExtScriptPath() + "custom-web.js")));
                    generateHome(applicationConfig, parser);
                    generateApplication(parser);
                    generateTest(parser);
                    generateApplicationi18nResource(applicationConfig, fileFilter);
                }
            }

            List<BaseEntity> webEntities = new ArrayList<>();
            if (appConfigData.isMonolith() || appConfigData.isMicroservice()) {
                Map<String, String> templateLib = getResource(getTemplatePath() + "entity-include-resources.zip");
                List<Entity> entities = entityMapping.getGeneratedEntity().collect(toList());
                if (!entities.isEmpty()) {
                    handler.append(Console.wrap(this.getClass(), "MSG_Copying_Entity_Files", FG_DARK_RED, BOLD, UNDERLINE));
                }

                for (Entity entity : entities) {
                    if (JavaClass.isAutoGenerated(entity.getClazz())) {
                        continue;
                    }
                    BaseEntity webEntity = getEntity(applicationConfig, entity);
                    if (webEntity != null) {
                        webEntities.add(webEntity);
                        webEntity.setUpgrade(appConfigData.isCompleteApplication() ? false
                                : webRoot.getFileObject("app/entities/" + webEntity.getEntityFolderName()) != null
                        );
                        EntityConfig entityConfig = getEntityConfig(entity);
                        generateEntity(applicationConfig, entityConfig, webEntity, templateLib);
                        generateEntityTest(applicationConfig, entityConfig, webEntity);
                        generateEntityi18nResource(applicationConfig, fileFilter, webEntity);
                    }
                }
                generateEnumi18nResource(applicationConfig, fileFilter);
                applicationConfig.setEntities(webEntities);
            }
            updateNeedle(applicationConfig, webEntities);


        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

//    public void installYarn(){
//        handler.append(Console.wrap(WebGenerator.class, "YARN_INSTALL", FG_DARK_RED, BOLD, UNDERLINE));
//        String displayName = String.format("yarn (%s)", getProjectDisplayName(project));
//        ProcessBuilder processBuilder = ProcessBuilder.getLocal();
//        processBuilder.setExecutable("yarn");
//        processBuilder.setArguments(asList("install"));
//        processBuilder.setWorkingDirectory(FileUtil.toFile(project.getProjectDirectory()).getAbsolutePath());
//        ExecutionDescriptor executionDescriptor = ExternalExecutable.DEFAULT_EXECUTION_DESCRIPTOR;//getExecutionDescriptor(executionDescriptor, outProcessorFactory);
//        Future<Integer> task = ExecutionService.newService(processBuilder, executionDescriptor, displayName).run();
//    }
    
    protected void generateEntity(BaseApplicationConfig applicationConfig,
            EntityConfig config, BaseEntity entity, Map<String, String> templateLib) throws IOException {
        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(entity);
        parser.addContext(config);
        parser.setImportTemplate(templateLib);
        parser.eval(readString(getClass().getResourceAsStream(getExtScriptPath() + "custom-entity.js")));

        copyDynamicResource(parser.getParserManager(), getTemplatePath() + "entity-resources.zip", webRoot, getEntityPathResolver(entity), handler);

        try {
            parser.parse("mock");
        } catch (ScriptException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    protected void addDependencies(Reader pom) {
        BuildManager.getInstance(project)
                .copy(pom)
                .commit();
    }

    protected void updateNeedle(BaseApplicationConfig applicationConfig, List<BaseEntity> webEntities) {
        for (NeedleFile needleFile : getNeedleFiles(applicationConfig)) {
            if (appConfigData.isMonolith()) {
                if (!appConfigData.isCompleteApplication() && !needleFile.forEntity()) {
                    continue;
                }
            } else if (appConfigData.isMicroservice()) {
                if (!needleFile.forEntity()) {
                    continue;
                }
            } else if (appConfigData.isGateway()) {
                if (needleFile.forEntity()) {
                    continue;
                }
            }

            for (String file : needleFile.getFile()) {
                needleFile.getNeedles().forEach(needle
                        -> insertNeedle(file.startsWith("/") ? projectRoot : webRoot,
                                file, needle.getInsertPointer(),
                                needle.getTemplate(applicationConfig, needleFile.forEntity() ? webEntities : null),
                                handler)
                );
            }
        }
    }

    protected void generateApplication(EJSParser parser) throws IOException {
        handler.append(Console.wrap(this.getClass(), "MSG_Copying_Application_Files", FG_DARK_RED, BOLD, UNDERLINE));
        copyDynamicResource(parser.getParserManager(), getTemplatePath() + "web-resources.zip", webRoot, PATH_RESOLVER, handler);
    }

    protected void generateEntityTest(BaseApplicationConfig applicationConfig, EntityConfig config, BaseEntity entity) throws IOException {
        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(entity);
        parser.addContext(config);
        parser.eval(readString(getClass().getResourceAsStream(getExtScriptPath() + "custom-entity.js")));
        copyDynamicResource(parser.getParserManager(), getTemplatePath() + "entity-unit-test.zip", testRoot, getEntityPathResolver(entity), handler);
        if (webData.isProtractorTest()) {
            copyDynamicResource(parser.getParserManager(), getTemplatePath() + "entity-e2e-test.zip", testRoot, getEntityPathResolver(entity), handler);
        }
    }

    protected void generateTest(EJSParser parser) throws IOException {
        copyDynamicResource(parser.getParserManager(), getTemplatePath() + "unit-test.zip", testRoot, PATH_RESOLVER, handler);
        if (webData.isProtractorTest()) {
            copyDynamicResource(parser.getParserManager(), getTemplatePath() + "e2e-test.zip", testRoot, PATH_RESOLVER, handler);
        }
    }

    @Override
    public void postExecute() {
        if (appConfigData.isMonolith() || appConfigData.isGateway()) {
            appConfigData.getEnvironment("dev")
                    .addProfileAndActivate("webpack", project);
        }
    }
        
    protected void generateHome(BaseApplicationConfig applicationConfig, EJSParser parser) throws IOException {
        copyDynamicResource(parser.getParserManager(), getTemplatePath() + "project-resources.zip", projectRoot, identity(), handler);
//        installYarn();
        try (Reader sourceReader = new InputStreamReader(loadResource(getPomPath()))) {
            try (Reader targetReader = new StringReader(parser.parse(sourceReader))) {
                addDependencies(targetReader);
            } catch (ScriptException ex) {
                Exceptions.printStackTrace(ex);
                System.out.println("Error in template : " + getPomPath());
            }
        }

        String clientPackageManagerTitle = StringHelper.firstUpper(applicationConfig.getClientPackageManager());
        handler.info("Installation prerequisites", "Maven v3.5.0, Node.js, " + clientPackageManagerTitle);
        appConfigData.getEnvironment("dev")
                .addPreCommand(applicationConfig.getClientPackageManager() + " install");
    }

    protected Function<String, String> getEntityPathResolver(BaseEntity entity) {
        Function<String, String> ENTITY_PATH_RESOLVER = templatePath -> {
            if (!fileFilter.isEnable(templatePath)) {
                return null;
            }
            if (templatePath.contains("entity-management")) {
                templatePath = templatePath.replace("entity-management", entity.getEntityFolderName() + '/' + entity.getEntityServiceFileName());
            } else if (templatePath.contains("entity.model.ts")) {
                templatePath = templatePath.replace("entities/entity", "shared/model/" + entity.getEntityModelFileName());
            } else if (templatePath.contains("entity.")
                    || templatePath.contains("entity-")) {
                templatePath = templatePath.replace("entity", entity.getEntityFolderName() + '/' + entity.getEntityServiceFileName());
            } else if (templatePath.contains("index.ts")) {
                templatePath = templatePath.replace("index", entity.getEntityFolderName() + "/index");
            }

            return templatePath;
        };
        return ENTITY_PATH_RESOLVER;
    }

    protected abstract List<NeedleFile> getNeedleFiles(BaseApplicationConfig applicationConfig);

    protected abstract String getPomPath();

    protected abstract String getExtScriptPath();
}
