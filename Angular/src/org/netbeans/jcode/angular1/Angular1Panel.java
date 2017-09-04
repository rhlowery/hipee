/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jcode.angular1;

import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import org.netbeans.jcode.ng.main.*;
import org.netbeans.jcode.stack.config.panel.*;
import org.openide.util.NbBundle;

/**
 *
 * @author Gaurav Gupta
 */
public class Angular1Panel extends LayerConfigPanel<AngularData> {

    public Angular1Panel() {
        initComponents();
    }

    @Override
    public boolean hasError() {
        warningLabel.setText("");
        setModule(getModule().replaceAll("[^a-zA-Z0-9]+", EMPTY));
        if (StringUtils.isBlank(getModule())) {
            warningLabel.setText(NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.invalidModule.message"));
            return true;
        }
        if (StringUtils.isBlank(getApplicationTitle())) {
            warningLabel.setText(NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.invalidTitle.message"));
            return true;
        }
        return false;
    }


    @Override
    public void read() {
        AngularData data = this.getConfigData();
        if (StringUtils.isNotBlank(data.getModule())) {
            setModule(data.getModule());
        }
        if (StringUtils.isNotBlank(data.getApplicationTitle())) {
            setApplicationTitle(data.getApplicationTitle());
        }
    }

    @Override
    public void store() {
        this.getConfigData().setModule(getModule());
        this.getConfigData().setApplicationTitle(getApplicationTitle());
    }

    @Override
    public void init(String folder, Project project, SourceGroup sourceGroup) {
        setModule(kebabCase(firstLower(project.getProjectDirectory().getName())));
        setApplicationTitle(startCase(project.getProjectDirectory().getName()));
    }
    
    public String getModule() {
        return angularModuleTextField.getText().trim();
    }
    private void setModule(String module) {
        angularModuleTextField.setText(module);
    }
    public String getApplicationTitle() {
        return appTitleTextField.getText().trim();
    }
    private void setApplicationTitle(String module) {
        appTitleTextField.setText(module);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        warningPanel = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        wrapperPanel2 = new javax.swing.JPanel();
        angularModuleLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        angularModuleTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        wrapperPanel1 = new javax.swing.JPanel();
        appTitleLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        titleStartLabel = new javax.swing.JLabel();
        appTitleTextField = new javax.swing.JTextField();
        titleEndLabel = new javax.swing.JLabel();

        warningPanel.setLayout(new java.awt.BorderLayout(10, 0));

        warningLabel.setForeground(new java.awt.Color(200, 0, 0));
        warningLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(warningLabel, org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.warningLabel.text")); // NOI18N
        warningPanel.add(warningLabel, java.awt.BorderLayout.CENTER);

        jPanel1.setPreferredSize(new java.awt.Dimension(217, 90));
        jPanel1.setLayout(new java.awt.GridLayout(3, 0, 0, 15));

        wrapperPanel2.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(angularModuleLabel, org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.angularModuleLabel.text")); // NOI18N
        wrapperPanel2.add(angularModuleLabel, java.awt.BorderLayout.LINE_START);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.jLabel1.text")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 14));
        jPanel3.add(jLabel1);

        angularModuleTextField.setText(org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.angularModuleTextField.text")); // NOI18N
        angularModuleTextField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                angularModuleTextFieldPropertyChange(evt);
            }
        });
        jPanel3.add(angularModuleTextField);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.jLabel2.text")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(20, 14));
        jPanel3.add(jLabel2);

        wrapperPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        jPanel1.add(wrapperPanel2);

        wrapperPanel1.setLayout(new java.awt.BorderLayout(10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(appTitleLabel, org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.appTitleLabel.text")); // NOI18N
        wrapperPanel1.add(appTitleLabel, java.awt.BorderLayout.LINE_START);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        titleStartLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(titleStartLabel, org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.titleStartLabel.text")); // NOI18N
        titleStartLabel.setPreferredSize(new java.awt.Dimension(45, 14));
        jPanel2.add(titleStartLabel);

        appTitleTextField.setText(org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.appTitleTextField.text")); // NOI18N
        appTitleTextField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                appTitleTextFieldPropertyChange(evt);
            }
        });
        jPanel2.add(appTitleTextField);

        titleEndLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(titleEndLabel, org.openide.util.NbBundle.getMessage(Angular1Panel.class, "Angular1Panel.titleEndLabel.text")); // NOI18N
        titleEndLabel.setPreferredSize(new java.awt.Dimension(45, 14));
        jPanel2.add(titleEndLabel);

        wrapperPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.add(wrapperPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(warningPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(213, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(289, Short.MAX_VALUE)
                    .addComponent(warningPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void appTitleTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_appTitleTextFieldPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_appTitleTextFieldPropertyChange

    private void angularModuleTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_angularModuleTextFieldPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_angularModuleTextFieldPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel angularModuleLabel;
    private javax.swing.JTextField angularModuleTextField;
    private javax.swing.JLabel appTitleLabel;
    private javax.swing.JTextField appTitleTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel titleEndLabel;
    private javax.swing.JLabel titleStartLabel;
    private javax.swing.JLabel warningLabel;
    private javax.swing.JPanel warningPanel;
    private javax.swing.JPanel wrapperPanel1;
    private javax.swing.JPanel wrapperPanel2;
    // End of variables declaration//GEN-END:variables


}
