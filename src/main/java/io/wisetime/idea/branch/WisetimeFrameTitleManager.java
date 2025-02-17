package io.wisetime.idea.branch;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.wm.impl.PlatformFrameTitleBuilder;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

/**
 * To simplify QA on Windows, window title can be enabled:
 * Help -> Edit Custom Properties -> ide.win.frame.decoration=false
 */
public class WisetimeFrameTitleManager extends PlatformFrameTitleBuilder {

    /**
     * Window title format pre IntelliJ IDEA 2020.1.
     */
    private static final String WINDOW_TITLE_BRACKETS_PATTERN = "^(.*?)\\[.+?](.*)";

    @Override
    public String getProjectTitle(@NotNull Project project) {
        final String currentBranch = Optional.ofNullable(project.getService(BranchHelper.class))
                .map(BranchHelper::getCurrentBranchName)
                // return empty string if branch name is unavailable
                .orElse("");
        return getProjectTitleWithBranch(project, currentBranch);
    }

    String getProjectTitleWithBranch(Project project, String branchName) {
        String projectTitle = getProjectDisplayName(project);
        if (branchName != null && !branchName.isEmpty()) {
            if (projectTitle.matches(WINDOW_TITLE_BRACKETS_PATTERN)) {
                // if an existing title ends with context data inside square brackets, replace with the updated branch title
                projectTitle = projectTitle.replaceAll(WINDOW_TITLE_BRACKETS_PATTERN, "$1[" + branchName + "]$2");
            } else {
                projectTitle += " [" + branchName + "]";
            }
        }
        return projectTitle;
    }

    /**
     * 优先使用项目文件夹名字.这样可以通过重命名项目文件夹来重命名.
     */
    private String getProjectDisplayName(Project project) {
        String basePath = project.getBasePath();
        if (basePath == null) {
            return project.getName();
        }
        final String[] splits = basePath.split("/");
        if (splits.length == 0) {
            return project.getName();
        }

        final String projectLeafDir = splits[splits.length - 1];
        return projectLeafDir;
    }

}
