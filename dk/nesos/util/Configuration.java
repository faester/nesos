package dk.nesos.util;

import java.util.*;
import java.io.*;

public final class Configuration {
    private static final String mapSizeKey = "mapsize";
    private static final String terrainFilenameKey = "terrainfilename";
    private static final String fillKey = "fill";
    private static final String cullFaceKey = "cullface";
    private static final String widthKey = "width";
    private static final String heightKey = "height";
    private static final String bitsPerPixelKey = "bitsperpixel";
    private static final String lightingKey = "lighting";
    private static final String antiAliasingKey = "antialiasing";
    private static final String smoothKey = "smooth";
    private static final String loadTerrainKey = "loadterrain";
    private static final String waterKey = "water";
    private static final String vegetationKey = "vegetation";
    private static final String debugEnabledKey = "debug";
    private static final String debugMfKey = "debugmf";
    private static final String debugNdhbKey = "debugndhb";
    private static final String terrainSeedKey = "terrainseed";
    private static final String logFilenameKey = "logfilename";
    private static final String vsyncKey = "vsync";
    private static final String riversAdjustTerrainKey = "riversadjustterrain";
    private static final String numberOfSourcesKey = "sources";

    private static int mapSize = 256;
    private static String terrainFilename = "res/terrain/sample257_subdiv01.raw";
    private static boolean fill = true;
    private static boolean smooth = true;
    private static boolean cullFace = true;
    private static int width = 1024;
    private static int height = 768;
    private static int bitsPerPixel = 32;
    private static boolean lighting = true;
    private static boolean antiAliasing = true;
    private static boolean loadTerrain = false;
    private static boolean water = true;
    private static boolean vegetation = true;
    private static boolean debug = true;
    private static boolean debugMf = true;
    private static boolean debugNdhb = true;
    private static int terrainSeed = 0;
    private static String logFilename = "log.txt";
    private static boolean vsync = false;
    private static boolean riversAdjustTerrain = false;
    private static int numberOfSources = 10;
    
    private static Properties properties = new Properties();
    
    static {
    	String configProperty = System.getProperty("config");
    	if (configProperty == null) {
    		System.err.println("Please specify config (eg. -Dconfig=\"asset/config/my_config\")");
    		System.exit(1);
    	} // if no config
    	loadFile(configProperty);
    }
    
    public static int getNumberOfSources() {
        return numberOfSources;
    }

    /**
     * Will set the properties in accordance with the setting in the file filename. 
     * No error reporting on unknown settings or illegal values.
     * @param filename configuration file to read
     * @throws IOException if the configuration file is unknown
     */
    
    public static void loadFile(String filename) {
    	File file = new File(filename);
        try {
            InputStream in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
            System.err.println("IOException trying to load " + filename + "!!!");
            System.exit(1);
        } // try catch
        
        mapSize = Integer.parseInt(properties.getProperty(mapSizeKey, Integer.toString(mapSize)));
        terrainFilename = properties.getProperty(terrainFilenameKey, terrainFilename);
        fill = Boolean.parseBoolean(properties.getProperty(fillKey, Boolean.toString(fill)));
        smooth = Boolean.parseBoolean(properties.getProperty(smoothKey, Boolean.toString(smooth)));
        cullFace = Boolean.parseBoolean(properties.getProperty(cullFaceKey, Boolean.toString(cullFace)));
        width = Integer.parseInt(properties.getProperty(widthKey, Integer.toString(width)));
        height = Integer.parseInt(properties.getProperty(heightKey, Integer.toString(height)));
        bitsPerPixel = Integer.parseInt(properties.getProperty(bitsPerPixelKey, Integer.toString(bitsPerPixel)));
        lighting = Boolean.parseBoolean(properties.getProperty(lightingKey, Boolean.toString(lighting)));
        antiAliasing = Boolean.parseBoolean(properties.getProperty(antiAliasingKey, Boolean.toString(antiAliasing)));
        loadTerrain = Boolean.parseBoolean(properties.getProperty(loadTerrainKey, Boolean.toString(loadTerrain)));
        water = Boolean.parseBoolean(properties.getProperty(waterKey, Boolean.toString(water)));
        vegetation = Boolean.parseBoolean(properties.getProperty(vegetationKey, Boolean.toString(vegetation)));
        debug = Boolean.parseBoolean(properties.getProperty(debugEnabledKey, Boolean.toString(debug)));
        debugMf = Boolean.parseBoolean(properties.getProperty(debugMfKey, Boolean.toString(debugMf)));
        debugNdhb = Boolean.parseBoolean(properties.getProperty(debugNdhbKey, Boolean.toString(debugNdhb)));
        logFilename = properties.getProperty(logFilenameKey, logFilename);
        vsync = Boolean.parseBoolean(properties.getProperty(vsyncKey, Boolean.toString(vsync)));
        riversAdjustTerrain = Boolean.parseBoolean(properties.getProperty(riversAdjustTerrainKey, Boolean.toString(riversAdjustTerrain)));
        numberOfSources = Integer.parseInt(properties.getProperty(numberOfSourcesKey, Integer.toString(numberOfSources)));
        
        //Check if terrain seed has been set.
        if (properties.containsKey(terrainSeedKey)) {
            String seedStr = properties.getProperty(terrainSeedKey);
            if (seedStr.equals("random")) {
                Random rnd = new Random();
                terrainSeed = rnd.nextInt();
            } else {
                try {
                    terrainSeed = Integer.parseInt(seedStr);
                }
                catch (NumberFormatException e){
                    System.err.println("Could not parse terrain seed from ini file. ");
                    System.err.println("Seed sent was '" + seedStr + "'. Should be integer number of 'random'");
                    System.exit(1);
                }
            }
        }
    }

    public static boolean hasAntiAliasing() {
        return antiAliasing;
    }

    public static int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public static boolean hasCullFace() {
        return cullFace;
    }

    public static String getTerrainFilename() {
        return terrainFilename;
    }

    public static boolean hasFill() {
        return fill;
    }

    public static int getHeight() {
        return height;
    }

    public static boolean hasLighting() {
        return lighting;
    }

    public static int getMapSize() {
        return mapSize;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static boolean hasSmooth() {
        return smooth;
    }

    public static int getWidth() {
        return width;
    }
    
    public static boolean hasLoadTerrain() {
        return loadTerrain;
    }
    
    public static boolean hasWaterEnabled(){
        return water;
    }
    
    public static boolean hasVegetation(){
        return vegetation;
    }
    
    public static boolean getFill () {
        return fill;
    }

    public static int getTerrainSeed() {
        return terrainSeed;
    }
    
    public static boolean hasDebug() {
        return debug;
    }

    public static boolean hasDebugMf() {
        return debugMf;
    }

    public static String getLogFilename() {
        return logFilename;
    }
    
    public static boolean hasDebugNdhb() {
        return debugNdhb;
    }

    public static boolean hasVsync() {
        return vsync;
    }
    
    public static boolean hasRiversAdjustTerrain() {
        return riversAdjustTerrain;
    }
}
