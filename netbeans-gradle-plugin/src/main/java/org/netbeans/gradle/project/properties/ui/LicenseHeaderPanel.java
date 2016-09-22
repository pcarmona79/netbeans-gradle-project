package org.netbeans.gradle.project.properties.ui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.jtrim.utils.ExceptionHelper;
import org.netbeans.gradle.project.NbGradleProject;
import org.netbeans.gradle.project.api.config.ActiveSettingsQuery;
import org.netbeans.gradle.project.api.config.PropertyReference;
import org.netbeans.gradle.project.api.config.ui.ProfileBasedSettingsPage;
import org.netbeans.gradle.project.api.config.ui.ProfileEditor;
import org.netbeans.gradle.project.api.config.ui.ProfileEditorFactory;
import org.netbeans.gradle.project.api.config.ui.ProfileInfo;
import org.netbeans.gradle.project.api.config.ui.StoredSettings;
import org.netbeans.gradle.project.properties.LicenseHeaderInfo;
import org.netbeans.gradle.project.properties.NbGradleCommonProperties;
import org.netbeans.gradle.project.util.NbFileUtils;
import org.netbeans.gradle.project.util.NbSupplier;
import org.openide.filesystems.FileChooserBuilder;

@SuppressWarnings("serial")
public class LicenseHeaderPanel extends javax.swing.JPanel implements ProfileEditorFactory {
    private static final String ORGANIZATION_PROPERTY_NAME = "organization";

    private final NbSupplier<? extends Path> defaultDirProvider;

    private LicenseHeaderPanel(NbSupplier<? extends Path> defaultDirProvider) {
        ExceptionHelper.checkNotNullArgument(defaultDirProvider, "defaultDirProvider");

        this.defaultDirProvider = defaultDirProvider;

        initComponents();
    }

    public static ProfileBasedPanel createProfileBasedPanel(NbGradleProject project) {
        return ProfileBasedPanel.createPanel(project, new LicenseHeaderPanel(toDefaultDirProvider(project)));
    }

    public static ProfileBasedSettingsPage createSettingsPage(NbSupplier<? extends Path> defaultDirProvider) {
        LicenseHeaderPanel result = new LicenseHeaderPanel(defaultDirProvider);
        return new ProfileBasedSettingsPage(result, result);
    }

    private static NbSupplier<? extends Path> toDefaultDirProvider(final NbGradleProject project) {
        ExceptionHelper.checkNotNullArgument(project, "project");
        return new NbSupplier<Path>() {
            @Override
            public Path get() {
                return project.currentModel().getValue().getSettingsDir();
            }
        };
    }

    @Override
    public ProfileEditor startEditingProfile(ProfileInfo profileInfo, ActiveSettingsQuery profileQuery) {
        return new PropertyRefs(profileQuery);
    }

    private void displayLicenseHeaderInfo(final LicenseHeaderInfo info) {
        if (info == null) {
            jLicenseNameEdit.setText("");
            jLicenseTemplateEdit.setText("");
            jOrganizationEdit.setText("");
        }
        else {
            String organization = info.getProperties().get(ORGANIZATION_PROPERTY_NAME);
            jOrganizationEdit.setText(organization != null ? organization : "");

            jLicenseNameEdit.setText(info.getLicenseName());

            Path licenseTemplate = info.getLicenseTemplateFile();
            jLicenseTemplateEdit.setText(licenseTemplate != null ? licenseTemplate.toString() : "");
        }
    }

    private LicenseHeaderInfo getLicenseHeaderInfo() {
        String name = jLicenseNameEdit.getText().trim();
        if (name.isEmpty()) {
            return null;
        }

        String template = jLicenseTemplateEdit.getText().trim();
        Path templateFile = template.isEmpty() ? null : Paths.get(template);

        String organization = jOrganizationEdit.getText().trim();

        return new LicenseHeaderInfo(
                name,
                Collections.singletonMap(ORGANIZATION_PROPERTY_NAME, organization),
                templateFile);
    }

    private final class PropertyRefs implements ProfileEditor {
        private final PropertyReference<LicenseHeaderInfo> licenseHeaderInfoRef;

        public PropertyRefs(ActiveSettingsQuery settingsQuery) {
            this.licenseHeaderInfoRef = NbGradleCommonProperties.licenseHeaderInfo(settingsQuery);
        }

        @Override
        public StoredSettings readFromSettings() {
            return new StoredSettingsImpl(this);
        }

        @Override
        public StoredSettings readFromGui() {
            return new StoredSettingsImpl(this, LicenseHeaderPanel.this);
        }
    }

    private final class StoredSettingsImpl implements StoredSettings {
        private final PropertyRefs properties;
        private final LicenseHeaderInfo licenseHeaderInfo;

        public StoredSettingsImpl(PropertyRefs properties) {
            this.properties = properties;
            this.licenseHeaderInfo = properties.licenseHeaderInfoRef.tryGetValueWithoutFallback();
        }

        public StoredSettingsImpl(PropertyRefs properties, LicenseHeaderPanel panel) {
            this.properties = properties;
            this.licenseHeaderInfo = panel.getLicenseHeaderInfo();
        }

        @Override
        public void displaySettings() {
            displayLicenseHeaderInfo(licenseHeaderInfo);
        }

        @Override
        public void saveSettings() {
            properties.licenseHeaderInfoRef.setValue(licenseHeaderInfo);
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jOrganizationCaption = new javax.swing.JLabel();
        jOrganizationEdit = new javax.swing.JTextField();
        jLicenseNameCaption = new javax.swing.JLabel();
        jLicenseNameEdit = new javax.swing.JTextField();
        jLicenseTemplateCaption = new javax.swing.JLabel();
        jLicenseTemplateEdit = new javax.swing.JTextField();
        jBrowseButton = new javax.swing.JButton();
        jCaption = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jOrganizationCaption, org.openide.util.NbBundle.getMessage(LicenseHeaderPanel.class, "LicenseHeaderPanel.jOrganizationCaption.text")); // NOI18N

        jOrganizationEdit.setText(org.openide.util.NbBundle.getMessage(LicenseHeaderPanel.class, "LicenseHeaderPanel.jOrganizationEdit.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLicenseNameCaption, org.openide.util.NbBundle.getMessage(LicenseHeaderPanel.class, "LicenseHeaderPanel.jLicenseNameCaption.text")); // NOI18N

        jLicenseNameEdit.setText(org.openide.util.NbBundle.getMessage(LicenseHeaderPanel.class, "LicenseHeaderPanel.jLicenseNameEdit.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLicenseTemplateCaption, org.openide.util.NbBundle.getMessage(LicenseHeaderPanel.class, "LicenseHeaderPanel.jLicenseTemplateCaption.text")); // NOI18N

        jLicenseTemplateEdit.setText(org.openide.util.NbBundle.getMessage(LicenseHeaderPanel.class, "LicenseHeaderPanel.jLicenseTemplateEdit.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jBrowseButton, org.openide.util.NbBundle.getMessage(LicenseHeaderPanel.class, "LicenseHeaderPanel.jBrowseButton.text")); // NOI18N
        jBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jCaption, org.openide.util.NbBundle.getMessage(LicenseHeaderPanel.class, "LicenseHeaderPanel.jCaption.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCaption, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jOrganizationEdit)
                    .addComponent(jLicenseNameEdit)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jOrganizationCaption)
                            .addComponent(jLicenseNameCaption)
                            .addComponent(jLicenseTemplateCaption))
                        .addGap(0, 360, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLicenseTemplateEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBrowseButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCaption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jOrganizationCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jOrganizationEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLicenseNameCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLicenseNameEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLicenseTemplateCaption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLicenseTemplateEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBrowseButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private File tryGetDefaultDir() {
        Path result = defaultDirProvider.get();
        return result != null ? result.toFile() : null;
    }

    private void jBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowseButtonActionPerformed
        File defaultDir = tryGetDefaultDir();
        File initialDir = defaultDir;

        FileChooserBuilder dlgChooser = new FileChooserBuilder(
                LicenseHeaderPanel.class.getName() + (initialDir != null ? ("-" + initialDir.getName()) : ""));
        dlgChooser.setDefaultWorkingDirectory(initialDir);

        File f = dlgChooser.showOpenDialog();
        if (f == null || f.isDirectory()) {
            return;
        }

        File file = f.getAbsoluteFile();
        String relPath = defaultDir != null
                ? NbFileUtils.tryMakeRelative(defaultDir, file)
                : null;
        jLicenseTemplateEdit.setText(relPath != null ? relPath : file.getPath());

    }//GEN-LAST:event_jBrowseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBrowseButton;
    private javax.swing.JLabel jCaption;
    private javax.swing.JLabel jLicenseNameCaption;
    private javax.swing.JTextField jLicenseNameEdit;
    private javax.swing.JLabel jLicenseTemplateCaption;
    private javax.swing.JTextField jLicenseTemplateEdit;
    private javax.swing.JLabel jOrganizationCaption;
    private javax.swing.JTextField jOrganizationEdit;
    // End of variables declaration//GEN-END:variables
}