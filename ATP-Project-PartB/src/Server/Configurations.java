package Server;

import java.io.*;
import java.util.Properties;

public class Configurations {

    private static String path="src/resources/config.properties";
    private static Configurations instance=new Configurations();
    private Properties prop;

    private Configurations(){
        prop=new Properties();
        InsertBaseProps();
    }

    /**
     * Method sets base properties, which can be changed later
     */
    private void InsertBaseProps(){
        try {
            OutputStream out=new FileOutputStream(path);
            prop.setProperty("threadPoolSize","10");
            prop.setProperty("mazeGeneratingAlgorithm","MyMazeGenerator");
            prop.setProperty("mazeSearchingAlgorithm","BestFirstSearch");
            prop.store(out,null);
            out.flush();
            out.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     *Method gets the property by each name/key
     * @param propName property name/key
     * @return The value or "" if not found
     */
    public String getProperty(String propName){
        try {
            InputStream input = new FileInputStream(Configurations.getPath());
            prop.load(input);
            input.close();
            } catch (IOException e){
                    e.printStackTrace();
        }
        String defaultValue="";
        return prop.getProperty(propName,defaultValue);
    }

    /**
     * Method Updates properties file with a new pair of <key,value>
     * @param key prop's key
     * @param value prop's value
     */
    public void AddOrUpdateProperty(String key,String value){
        try {
            OutputStream out=new FileOutputStream(path);
            prop.setProperty(key,value);
            prop.store(out,null);
            out.flush();
            out.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String getPath() {
        return path;
    }

    public static Configurations getInstance(){
        return instance;
    }


}
