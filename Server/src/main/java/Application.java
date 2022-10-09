import GUI.ServerGUI;
import com.formdev.flatlaf.FlatIntelliJLaf;
import core.Impl.ServerImpl;

public class Application {

    public static void main(String[] args) {
        //Khởi tạo giao diện
        FlatIntelliJLaf.setup();
        ServerGUI.showAuthenticationFrame();
        ServerImpl.manager = new ServerGUI();
    }
}
