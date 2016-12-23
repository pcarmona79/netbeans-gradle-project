package org.netbeans.gradle.project.filesupport;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.jtrim.property.PropertyFactory;
import org.jtrim.property.PropertySource;
import org.jtrim.utils.ExceptionHelper;
import org.netbeans.gradle.project.newproject.NewProjectStrings;
import org.netbeans.gradle.project.script.GroovyScripts;
import org.netbeans.gradle.project.util.NbFileUtils;
import org.netbeans.gradle.project.validate.BackgroundValidator;
import org.netbeans.gradle.project.validate.Problem;
import org.netbeans.gradle.project.validate.Validator;
import org.openide.filesystems.FileObject;
import org.openide.loaders.TemplateWizard;

import static org.netbeans.gradle.project.validate.Validators.*;

@SuppressWarnings("serial")
class GradleTemplateWizardPanel extends javax.swing.JPanel {
    private static final Logger LOGGER = Logger.getLogger(GradleTemplateWizardPanel.class.getName());

    private final TemplateWizard wizard;
    private final AtomicBoolean started;
    private final BackgroundValidator bckgValidator;
    private final Path targetDir;

    private final PropertySource<String> fileNameStr;

    public GradleTemplateWizardPanel(TemplateWizard wizard) {
        ExceptionHelper.checkNotNullArgument(wizard, "wizard");

        this.started = new AtomicBoolean(false);
        this.wizard = wizard;
        this.bckgValidator = new BackgroundValidator();
        this.targetDir = NbFileUtils.asPath(getTargetDirObj(wizard));

        initComponents();

        this.jDestDirEdit.setText(getDisplayNameOfTargetDir(targetDir));
        this.fileNameStr = trimmedText(jFileNameEdit);
        this.fileNameStr.addChangeListener(new Runnable() {
            @Override
            public void run() {
                updateDestFileEdit();
            }
        });
        updateDestFileEdit();
    }

    private void updateDestFileEdit() {
        jDestFileEdit.setText(getResultFilePath().toString());
    }

    private static String getDisplayNameOfTargetDir(Path targetDir) {
        return targetDir != null ? targetDir.toString() : "";
    }

    private static FileObject getTargetDirObj(TemplateWizard wizard) {
        try {
            return wizard.getTargetFolder().getPrimaryFile();
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Failed to get target folder.", ex);
            return null;
        }
    }

    public void startValidation() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        Validator<String> emptyNameValidator = createNonEmptyValidator(
                Problem.Level.SEVERE,
                NewProjectStrings.getFileNameRequired());
        bckgValidator.addValidator(emptyNameValidator, fileNameStr);

        Validator<String> fileNameValidator = createFileNameWithExtensionValidator(
                Problem.Level.SEVERE,
                NewProjectStrings.getInvalidFileName());
        bckgValidator.addValidator(fileNameValidator, fileNameStr);

        Validator<Object> hasTargetDirValidator = new Validator<Object>() {
            @Override
            public Problem validateInput(Object inputType) {
                if (inputType != null) {
                    return null;
                }
                return Problem.severe(NewProjectStrings.getTargetFolderNotAvailable());
            }
        };
        bckgValidator.addValidator(hasTargetDirValidator, PropertyFactory.constSource(targetDir));

        connectWizardDescriptorToProblems(bckgValidator, wizard);
    }

    private static String toGradleFileName(String rawFileName) {
        String ext = GroovyScripts.EXTENSION;
        if (rawFileName.toLowerCase(Locale.ROOT).endsWith(ext)) {
            return rawFileName.substring(0, rawFileName.length() - ext.length()) + ext;
        }
        else if (rawFileName.endsWith(".")) {
            return rawFileName + GroovyScripts.EXTENSION_WITHOUT_DOT;
        }
        else {
            return rawFileName + ext;
        }
    }

    private Path getResultFilePath() {
        String fileName = toGradleFileName(fileNameStr.getValue());
        return targetDir != null ? targetDir.resolve(fileName) : Paths.get(fileName);
    }

    public GradleTemplateWizardConfig getConfig() {
        return new GradleTemplateWizardConfig(getResultFilePath());
    }

    public boolean containsValidData() {
        return bckgValidator.isValid();
    }

    public void addChangeListener(ChangeListener listener) {
        bckgValidator.currentProblemForSwing().addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        bckgValidator.currentProblemForSwing().removeChangeListener(listener);
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

        jFileNameLabel = new javax.swing.JLabel();
        jFileNameEdit = new javax.swing.JTextField();
        jDestDirLabel = new javax.swing.JLabel();
        jDestDirEdit = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jDestFileLabel = new javax.swing.JLabel();
        jDestFileEdit = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(jFileNameLabel, org.openide.util.NbBundle.getMessage(GradleTemplateWizardPanel.class, "GradleTemplateWizardPanel.jFileNameLabel.text")); // NOI18N

        jFileNameEdit.setText(org.openide.util.NbBundle.getMessage(GradleTemplateWizardPanel.class, "GradleTemplateWizardPanel.jFileNameEdit.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jDestDirLabel, org.openide.util.NbBundle.getMessage(GradleTemplateWizardPanel.class, "GradleTemplateWizardPanel.jDestDirLabel.text")); // NOI18N

        jDestDirEdit.setEditable(false);
        jDestDirEdit.setText(org.openide.util.NbBundle.getMessage(GradleTemplateWizardPanel.class, "GradleTemplateWizardPanel.jDestDirEdit.text")); // NOI18N
        jDestDirEdit.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jDestFileLabel, org.openide.util.NbBundle.getMessage(GradleTemplateWizardPanel.class, "GradleTemplateWizardPanel.jDestFileLabel.text")); // NOI18N

        jDestFileEdit.setEditable(false);
        jDestFileEdit.setText(org.openide.util.NbBundle.getMessage(GradleTemplateWizardPanel.class, "GradleTemplateWizardPanel.jDestFileEdit.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFileNameEdit)
                    .addComponent(jDestDirEdit)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDestDirLabel)
                            .addComponent(jFileNameLabel)
                            .addComponent(jDestFileLabel))
                        .addGap(0, 273, Short.MAX_VALUE))
                    .addComponent(jDestFileEdit))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jDestDirLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDestDirEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFileNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFileNameEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDestFileLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDestFileEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jDestDirEdit;
    private javax.swing.JLabel jDestDirLabel;
    private javax.swing.JTextField jDestFileEdit;
    private javax.swing.JLabel jDestFileLabel;
    private javax.swing.JTextField jFileNameEdit;
    private javax.swing.JLabel jFileNameLabel;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
