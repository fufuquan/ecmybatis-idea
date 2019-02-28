package me.gaigeshen.idea.ecmybatis;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author gaigeshen
 * @since 02/24 2019
 */
public class ConfigurePackageDialog extends DialogWrapper {

  private ComboBox<Project> projectComboBox;
  private ComboBox<Module> moduleComboBox;
  private JTextField domainPackageField;
  private JTextField daoPackageField;
  private JTextField mapperDirectoryField;

  private JButton domainPackageButton;
  private JButton daoPackageButton;
  private JButton mapperDirectoryButton;

  private Project project;
  private Module module;
  private PsiPackage domainPackage;
  private PsiPackage daoPackage;
  private VirtualFile mapperDirectory;

  public ConfigurePackageDialog() {
    super(true);
    init();
    setTitle("Configure Packages");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;

    initializeProjectsField(panel, gbc);
    initializeModulesField(panel, gbc);
    initializeDomainPackageField(panel, gbc);
    initializeDaoPackageField(panel, gbc);
    initializeMapperDirectoryField(panel, gbc);

    return panel;
  }

  private void initializeProjectsField(JComponent container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Project:");
    projectComboBox = new ComboBox<>();
    Project[] projects = ProjectManager.getInstance().getOpenProjects();
    projectComboBox.setModel(new DefaultComboBoxModel<>(projects));
    Project project = (Project) projectComboBox.getSelectedItem();
    if (project != null) {
      this.project = project;
    }
    gbc.gridx = 0;
    gbc.gridy = 0;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 0;
    container.add(projectComboBox, gbc);
    projectComboBox.addItemListener(e -> {
      this.project = (Project) e.getItem();
      moduleComboBox.removeAllItems();
      moduleComboBox.setModel(new DefaultComboBoxModel<>(ModuleManager.getInstance(this.project).getModules()));
    });
  }

  private void initializeModulesField(JPanel container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Module:");
    moduleComboBox = new ComboBox<>();
    gbc.gridx = 0;
    gbc.gridy = 1;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    container.add(moduleComboBox, gbc);
    moduleComboBox.addItemListener(e -> {
      module = (Module) e.getItem();
      domainPackageField.setText("");
      daoPackageField.setText("");
      mapperDirectoryField.setText("");
    });
    Project selected = (Project) projectComboBox.getModel().getSelectedItem();
    if (selected != null) {
      moduleComboBox.setModel(new DefaultComboBoxModel<>(ModuleManager.getInstance(selected).getModules()));
      Module module = (Module) moduleComboBox.getSelectedItem();
      if (module != null) {
        this.module = module;
      }
    }
  }

  private void initializeDomainPackageField(JPanel container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Domain Package:");
    domainPackageField = new JTextField();
    domainPackageField.setEditable(false);
    domainPackageButton = new JButton("Browse...");
    domainPackageButton.addActionListener(e -> {
      if (module == null) {
        Messages.showWarningDialog("Please selecte module", "Warning");
        return;
      }
      PackageChooserDialog dialog = new PackageChooserDialog("Choose Domain Package", module);
      if (dialog.showAndGet()) {
        domainPackage = dialog.getSelectedPackage();
        if (domainPackage != null) {
          String packageName = domainPackage.getQualifiedName();
          domainPackageField.setText(packageName.isEmpty() ? "(default)": packageName);
        }
      }
    });
    gbc.gridx = 0;
    gbc.gridy = 2;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 2;
    container.add(domainPackageField, gbc);
    gbc.gridx = 2;
    gbc.gridy = 2;
    container.add(domainPackageButton, gbc);
  }

  private void initializeDaoPackageField(JPanel container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Dao Package:");
    daoPackageField = new JTextField();
    daoPackageField.setEditable(false);
    daoPackageButton = new JButton("Browse...");
    daoPackageButton.addActionListener(e -> {
      if (module == null) {
        Messages.showWarningDialog("Please selecte module", "Warning");
        return;
      }
      PackageChooserDialog dialog = new PackageChooserDialog("Choose Dao Package", module);
      if (dialog.showAndGet()) {
        daoPackage = dialog.getSelectedPackage();
        if (daoPackage != null) {
          String packageName = daoPackage.getQualifiedName();
          daoPackageField.setText(packageName.isEmpty() ? "(default)": packageName);
        }
      }
    });
    gbc.gridx = 0;
    gbc.gridy = 3;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 3;
    container.add(daoPackageField, gbc);
    gbc.gridx = 2;
    gbc.gridy = 3;
    container.add(daoPackageButton, gbc);
  }

  private void initializeMapperDirectoryField(JPanel container, GridBagConstraints gbc) {
    JLabel label = new JLabel("Mapper Directory:");
    mapperDirectoryField = new JTextField();
    mapperDirectoryField.setEditable(false);
    mapperDirectoryButton = new JButton("Browse...");
    mapperDirectoryButton.addActionListener(e -> {
      FileChooser.chooseFile(new FileChooserDescriptor(false, true,
              false, false, false, false),
              project, null, vf -> {
        if (vf.isValid()) {
          mapperDirectory = vf;
          mapperDirectoryField.setText(vf.getPath());
        }
      });
    });
    gbc.gridx = 0;
    gbc.gridy = 4;
    container.add(label, gbc);
    gbc.gridx = 1;
    gbc.gridy = 4;
    container.add(mapperDirectoryField, gbc);
    gbc.gridx = 2;
    gbc.gridy = 4;
    container.add(mapperDirectoryButton, gbc);
  }

  public Project getProject() {
    return project;
  }

  public Module getModule() {
    return module;
  }

  public PsiPackage getDomainPackage() {
    return domainPackage;
  }

  public PsiPackage getDaoPackage() {
    return daoPackage;
  }

  public VirtualFile getMapperDirectory() {
    return mapperDirectory;
  }
}
