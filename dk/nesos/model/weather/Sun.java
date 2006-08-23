package dk.nesos.model.weather;

import org.lwjgl.util.vector.*;

// main source of light for the world
// directional inifinite light source in GL
// has lense flare effect based on camera location / direction

public class Sun {
    Vector3f position;
    Vector3f color; //Dependent on proximity to horizon
    
    //Needs to move defined by some function of world time...

}
