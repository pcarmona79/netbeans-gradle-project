package org.netbeans.gradle.project.tasks.vars;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.gradle.model.util.CollectionUtils;
import org.netbeans.gradle.project.NbGradleProject;
import org.netbeans.gradle.project.api.entry.ProjectPlatform;
import org.netbeans.gradle.project.api.task.TaskVariable;
import org.netbeans.gradle.project.api.task.TaskVariableMap;
import org.netbeans.gradle.project.java.test.SpecificTestClass;
import org.netbeans.gradle.project.java.test.SpecificTestcase;
import org.netbeans.gradle.project.java.test.TestTaskName;
import org.netbeans.gradle.project.tasks.vars.CachingVariableMap.ValueGetter;
import org.netbeans.gradle.project.tasks.vars.CachingVariableMap.VariableDef;
import org.netbeans.gradle.project.tasks.vars.CachingVariableMap.VariableValue;
import org.netbeans.gradle.project.util.StringUtils;
import org.netbeans.spi.project.SingleMethod;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;

public enum StandardTaskVariable {
    PROJECT_PATH_NOT_NORMALIZED("project-path", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            String uniqueName = project.currentModel().getValue().getMainProject().getProjectFullName();
            return new VariableValue(uniqueName);
        }
    }),
    PROJECT_PATH_NORMALIZED("project", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            String uniqueName = project.currentModel().getValue().getMainProject().getProjectFullName();
            if (":".equals(uniqueName)) { // This is the root project.
                uniqueName = "";
            }
            return new VariableValue(uniqueName);
        }
    }),
    SELECTED_CLASS("selected-class", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            return getOneValue(getSelectedClasses(project, actionContext));
        }
    }),
    SELECTED_FILE("selected-file", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            FileObject fileObject = getFileOfContext(actionContext);
            if (fileObject == null) {
                return VariableValue.NULL_VALUE;
            }

            File file = FileUtil.toFile(fileObject);
            if (file == null) {
                return VariableValue.NULL_VALUE;
            }

            return new VariableValue(file.getPath());
        }
    }),
    TEST_FILE_PATH("test-file-path", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            String selectedClass = variables.tryGetValueForVariable(SELECTED_CLASS.getVariable());
            return new VariableValue(deduceFromClass(selectedClass));
        }

        private String deduceFromClass(String selectedClass) {
            return selectedClass != null
                    ? selectedClass.replace('.', '/')
                    : null;
        }
    }),
    TEST_METHOD("test-method", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            return getOneValue(getMethodReplaceVariables(variables, project, actionContext));
        }
    }),
    PLATFORM_DIR("platform-dir", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            ProjectPlatform targetPlatform = project.getCommonProperties().targetPlatform().getActiveValue();
            FileObject rootFolder = targetPlatform != null ? targetPlatform.getRootFolder() : null;
            return new VariableValue(rootFolder != null
                    ? FileUtil.getFileDisplayName(rootFolder)
                    : null);
        }
    }),
    TEST_TASK_NAME("test-task-name", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            return new VariableValue(TestTaskName.getTaskName(actionContext));
        }
    }),
    TEST_TASK_NAME_CAPITAL("test-task-name-capital", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            String value = variables.tryGetValueForVariable(TEST_TASK_NAME.getVariable());
            return new VariableValue(value != null
                    ? StringUtils.capitalizeFirstCharacter(value)
                    : null);
        }
    }),
    CMD_LINE_ARGS("cmd-line-args", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            return VariableValue.EMPTY_VALUE;
        }
    }),
    JVM_LINE_ARGS("jvm-line-args", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            return VariableValue.EMPTY_VALUE;
        }
    }),
    TEST_CLASSES_ARGS("test-classes-args", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            return toTestArgument(getSelectedClasses(project, actionContext));
        }
    }),
    TEST_CLASSES_STARED_ARGS("test-classes-stared-args", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            List<String> classes = getSelectedClasses(project, actionContext);
            List<String> staredClasses = new ArrayList<>(classes.size());
            for (String cl: classes) {
                staredClasses.add(cl + "*");
            }
            return toTestArgument(staredClasses);
        }
    }),
    TEST_METHODS_ARGS("test-methods-args", new ValueGetter<NbGradleProject>() {
        @Override
        public VariableValue getValue(TaskVariableMap variables, NbGradleProject project, Lookup actionContext) {
            return toTestArgument(getMethodReplaceVariables(variables, project, actionContext));
        }
    });

    public static final String TEST_ARGUMENT = "--tests";

    private static final Logger LOGGER = Logger.getLogger(StandardTaskVariable.class.getName());
    private static final CachingVariableMap.VariableDefMap<NbGradleProject> TASK_VARIABLE_MAP
            = createStandardMap();

    private static String tryGetClassNameForFile(NbGradleProject project, FileObject file) {
        SourceGroup[] sourceGroups = ProjectUtils.getSources(project)
                .getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);

        String relFileName = null;
        for (SourceGroup group: sourceGroups) {
            FileObject sourceRoot = group.getRootFolder();
            String relPath = FileUtil.getRelativePath(sourceRoot, file);
            if (relPath != null) {
                // Remove the ".java" or ".groovy" from the end of
                // the file name
                relFileName = removeExtension(relPath);
                break;
            }
        }

        return relFileName != null ? relFileName.replace('/', '.') : null;
    }

    private static String tryGetMethodReplaceVariable(
            TaskVariableMap variables,
            NbGradleProject project,
            SingleMethod method) {

        String selectedClass = variables.tryGetValueForVariable(SELECTED_CLASS.getVariable());
        if (selectedClass == null) {
            selectedClass = tryGetClassNameForFile(project, method.getFile());
            if (selectedClass == null) {
                LOGGER.log(Level.INFO, "Could not find class file name for file {0}", method.getFile());
                return null;
            }
        }

        return selectedClass + "." + method.getMethodName();
    }

    private static List<String> getMethodReplaceVariables(
            TaskVariableMap variables,
            NbGradleProject project,
            Lookup actionContext) {

        Collection<? extends SingleMethod> methods = actionContext.lookupAll(SingleMethod.class);
        if (!methods.isEmpty()) {
            List<String> result = new ArrayList<>();
            for (SingleMethod method: methods) {
                String methodName = tryGetMethodReplaceVariable(variables, project, method);
                if (methodName != null) {
                    result.add(methodName);
                }
            }
            return result;
        }

        Collection<? extends SpecificTestcase> specificTestcases = actionContext.lookupAll(SpecificTestcase.class);
        if (!specificTestcases.isEmpty()) {
            List<String> result = new ArrayList<>();
            for (SpecificTestcase specificTestcase: specificTestcases) {
                result.add(specificTestcase.getTestIncludePattern());
            }
            return result;
        }

        return Collections.emptyList();
    }

    private static VariableValue getOneValue(List<String> values) {
        return values.isEmpty() ? VariableValue.NULL_VALUE : new VariableValue(values.get(0));
    }

    private static String removeExtension(String filePath) {
        int extSeparatorIndex = filePath.lastIndexOf('.');
        return extSeparatorIndex >= 0
                ? filePath.substring(0, extSeparatorIndex)
                : filePath;
    }

    private static List<FileObject> getFilesOfContext(Lookup context) {
        List<FileObject> files = new ArrayList<>();
        for (DataObject dataObj: context.lookupAll(DataObject.class)) {
            FileObject file = dataObj.getPrimaryFile();
            if (file != null && !file.isFolder()) {
                files.add(file);
            }
        }
        return files;
    }

    private static FileObject getFileOfContext(Lookup context) {
        List<FileObject> files = getFilesOfContext(context);
        return files.isEmpty() ? null : files.get(0);
    }

    public static TaskVariableMap createVarReplaceMap(
            NbGradleProject project, Lookup actionContext) {
        return new CachingVariableMap<>(TASK_VARIABLE_MAP, project, actionContext);
    }

    private final TaskVariable variable;
    private final ValueGetter<NbGradleProject> valueGetter;

    private StandardTaskVariable(String variableName, ValueGetter<NbGradleProject> valueGetter) {
        this.variable = new TaskVariable(variableName);
        this.valueGetter = valueGetter;
    }

    public TaskVariable getVariable() {
        return variable;
    }

    public String getVariableName() {
        return variable.getVariableName();
    }

    public String getScriptReplaceConstant() {
        return variable.getScriptReplaceConstant();
    }

    private static List<String> getSelectedClasses(NbGradleProject project, Lookup actionContext) {
        Collection<? extends SpecificTestClass> testClasses = actionContext.lookupAll(SpecificTestClass.class);
        if (!testClasses.isEmpty()) {
            List<String> result = new ArrayList<>();
            for (SpecificTestClass testClass: testClasses) {
                result.add(testClass.getTestClassName());
            }
            return result;
        }

        List<FileObject> files = getFilesOfContext(actionContext);
        if (files.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>(files.size());
        for (FileObject file: files) {
            String className = tryGetClassNameForFile(project, file);
            if (className != null) {
                result.add(className);
            }
        }
        return result;
    }

    private static VariableValue toTestArgument(List<String> values) {
        int valueCount = values.size();
        if (valueCount == 0) {
            return VariableValue.NULL_VALUE;
        }

        StringBuilder result = new StringBuilder(valueCount * 40);
        for (String value: values) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(TEST_ARGUMENT);
            result.append(' ');
            result.append(value);
        }
        return new VariableValue(result.toString());
    }

    private static CachingVariableMap.VariableDefMap<NbGradleProject> createStandardMap() {
        StandardTaskVariable[] variables = StandardTaskVariable.values();

        final Map<TaskVariable, CachingVariableMap.VariableDef<NbGradleProject>> result
                = CollectionUtils.newHashMap(variables.length);

        for (StandardTaskVariable variable: variables) {
            result.put(variable.getVariable(), variable.asVariableDef());
        }

        return new CachingVariableMap.VariableDefMap<NbGradleProject>() {
            @Override
            public CachingVariableMap.VariableDef<NbGradleProject> tryGetDef(TaskVariable variable) {
                return result.get(variable);
            }
        };
    }

    private VariableDef<NbGradleProject> asVariableDef() {
        return new VariableDef<>(variable, valueGetter);
    }
}
