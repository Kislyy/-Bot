package kr.kislyy.jagajindan;

import org.seleniumhq.jetty9.util.IO;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Setting {

    private String property;
    private File driver;
    boolean autoEnabled;
    String address;
    boolean hide;

    private final File setting;

    public Setting(File f) throws IOException {
        this.setting = f;
        f.getParentFile().mkdirs();

        File drivers = new File(f.getParentFile(), "drivers/drivers.zip");
        drivers.getParentFile().mkdir();
        if(drivers.exists()) {
            System.out.println("드라이버 압축 해제 시작");
            unZip(drivers.getAbsolutePath(), drivers.getParentFile().getAbsolutePath());
            drivers.delete();
            System.out.println("드라이버 압축 해제 완료");
        }

        if(!f.isFile() || f.length() < 1) {
            f.createNewFile();
            initFile(f);
        }

        BufferedReader br = Files.newBufferedReader(f.getAbsoluteFile().toPath());
        Properties p = new Properties();
        p.load(br);
        br.close();

        property = p.getProperty("property");
        driver = new File(drivers.getParentFile(), p.getProperty("driver"));
        autoEnabled = Boolean.parseBoolean(p.getProperty("auto"));
        hide = Boolean.parseBoolean(p.getProperty("hide"));
        address = p.getProperty("address");
    }

    public boolean isAutoEnabled() {
        return autoEnabled;
    }

    public String getProperty() {
        return property;
    }

    public File getDriver() {
        return driver;
    }

    public void save() {
        new Thread(()->{
            try {
                PrintWriter p = new PrintWriter(setting);
                p.println("property=" + property);
                p.println("driver=" + driver.getName());
                p.println("auto=" + autoEnabled);
                p.println("address=" + address);
                p.println("hide=" + hide);
                p.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void initFile(File f) throws FileNotFoundException {
        PrintWriter p = new PrintWriter(f);
        p.println("property=webdriver.chrome.driver");
        p.println("auto=false");
        p.println("hide=false");
        p.println("adress=");

        int cdVersion = getChromeVersion();
        if(cdVersion == -1)
            p.println("driver=cd78.exe");
        else {
            p.print("driver=");
            p.print("cd");
            p.print(cdVersion);
            p.println(".exe");
        }
        p.close();
    }

    private static int getChromeVersion() {
        File cf = new File("C:\\Program Files (x86)\\Google\\Chrome\\Application");
        for (File f : cf.listFiles()) {
           if(f.isDirectory()) {
               String name = f.getName();
               if(name.length() < 2) {
                   continue;
               }
               String sub = name.substring(0, 2);
               try {
                   int ver = Integer.parseInt(sub);
                   if(ver > 77) {
                       return ver;
                   }
               } catch (Exception e){
                   continue;
               }
           }
        }
        return -1;
    }

    public static void unZip(String filePath, String unZipPath) throws IOException{
        String zipFilePath = filePath;
        File zipFile = new File(zipFilePath);
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry ze = zis.getNextEntry();
        while(ze!=null){
            String entryName = ze.getName();
            File f = new File(unZipPath + '/' +  entryName);
            f.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            int len;
            byte buffer[] = new byte[1024];
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

}
