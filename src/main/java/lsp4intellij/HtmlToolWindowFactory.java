package lsp4intellij;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.NotNull;
import org.wso2.lsp4intellij.IntellijLanguageClient;
import org.wso2.lsp4intellij.utils.ApplicationUtils;

import javax.annotation.Nonnull;
import javax.swing.*;

public final class HtmlToolWindowFactory implements ToolWindowFactory {

  // private static WebView htmlViewer = null;
  private static JBCefBrowser browser = null;
  private static String htmlcontent = null;

  public static void show(@Nonnull Project project, @Nonnull String content) {

    // tool window not initialized/created yet?
    // TODO: or hidden -> lazyload: update HTML when user opens the panel
    if( browser == null){
      htmlcontent = content;
      showUiUpdate(project);
    }else {
      ApplicationUtils.invokeLater(() -> {
        browser.loadHTML(content);
      });

      /*
      Platform.runLater(()->{
        htmlViewer.getEngine().setJavaScriptEnabled(true);
        htmlViewer.getEngine().loadContent(content);
      });
      */
    }
  }

  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    init(project);
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent( getComponent(), "", false);
    toolWindow.getContentManager().addContent(content);
  }

  private static void showUiUpdate(Project project){
    ToolWindowManager.getInstance(project).notifyByBalloon("Magpie Control Panel", MessageType.INFO ,"Update" );
  }

  static JComponent getComponent(){
    return browser.getComponent();
  }

  public static void init(@NotNull Project project) {
    browser = new JBCefBrowser();
    browser.loadHTML(htmlcontent != null ? htmlcontent : "<html> Nothing to show. </html>");
    Disposer.register(ServiceManager.getService(IntellijLanguageClient.class), browser);

    /*
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).registerToolWindow("MagpieBridge Control Panel", false, ToolWindowAnchor.BOTTOM);
    toolWindow.getComponent().add( browser.getComponent());

    JFXPanel fxPanel = new JFXPanel();
    JComponent component = toolWindow.getComponent();
    Platform.setImplicitExit(false);
    Platform.runLater(() -> {
      Group root  =  new Group();
      Scene scene  =  new  Scene(root, javafx.scene.paint.Color.WHITE);
      htmlViewer = new WebView();
      htmlViewer.getEngine().loadContent("<html>Hello World :)</html>");
      htmlViewer.setPrefWidth( toolWindow.getComponent().getWidth() );
      root.getChildren().add(htmlViewer);
      fxPanel.setScene(scene);
    });
    component.getParent().add(fxPanel);
    */

  }
}