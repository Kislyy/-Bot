package kr.kislyy.jagajindan;

import javafx.scene.control.Alert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import sun.misc.Lock;

import java.util.concurrent.atomic.AtomicReference;

public class Client {

    private final App app;
    private ChromeDriver driver;
    private Lock lock = new Lock();

    public Client(App app) {
        this.app = app;
        ChromeOptions o = new ChromeOptions();
        o.setHeadless(app.getSetting().hide);
        this.driver = new ChromeDriver(o);
        if(app.getSetting().autoEnabled) {
            doMacro(app.getSetting().address);
        }
    }

    public synchronized void refresh() {
        new Thread(()->{
            try {
                lock.lock();
                driver.close();
                ChromeOptions o = new ChromeOptions();
                o.setHeadless(app.getSetting().hide);
                this.driver = new ChromeDriver(o);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
    }

    public void doMacro(String address) {
        AtomicReference<String> msg = new AtomicReference<>();
        AtomicReference<Boolean> ended = new AtomicReference<>(false);
        try {
            lock.lock();
            new Thread(()->{
                try {
                    msg.set(macro(address));
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    ended.set(true);
                    lock.unlock();
                }
            }).start();

            boolean end;
            int max = 10000;
            while (!(end = ended.get())) {
                Thread.sleep(1);
                if(--max == 0) break;
            }

            if(msg.get() != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("자가진단 마침");
                alert.setHeaderText("성공적으로 자가진단을 수행하였습니다.");
                alert.setContentText(msg.get() + "\n\n아래 버튼을 클릭 시 5초 뒤 종료됩니다.");
                alert.showAndWait();
                driver.close();
                new Thread(()->{
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }).start();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("자가진단 오류");
                alert.setHeaderText("자가진단 수행 중 오류가 발생하였습니다.");
                alert.showAndWait();
            }


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String macro(String address) throws Throwable {
        driver.get(address);
        doCheck("//*[@id=\"rspns011\"]");
        doCheck("//*[@id=\"rspns02\"]");
        doCheck("//*[@id=\"rspns070\"]");
        doCheck("//*[@id=\"rspns080\"]");
        doCheck("//*[@id=\"rspns090\"]");
        doCheck("//*[@id=\"btnConfirm\"]");

        WebElement e;
        int max = 10000;
        while ((e = getEle("//*[@id=\"container\"]/div/div/div/div[3]/p")) == null){
            Thread.sleep(1);
            if(--max == 0) break;
        }

        String txt = e.getText();
        if(txt != null && txt.length() > 0) {
            System.out.println("--------------------------------------");
            System.out.println("성공적으로 자가진단을 수행하였습니다.");
            System.out.println("--------------------------------------");
            System.out.println(txt);
            System.out.println("--------------------------------------");
            return txt;
        }
        return null;
    }

    private void doCheck(String xpath) throws InterruptedException {
        try {
            WebElement e = driver.findElementByXPath(xpath);
            e.click();
        } catch (NoSuchElementException e) {
            System.err.println("웹 Element " + xpath + "를 찾을 수 없습니다.");
        }
        Thread.sleep(1);
    }

    private WebElement getEle(String xpath) {
        try {
            WebElement e = driver.findElementByXPath(xpath);
            return e;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

}
