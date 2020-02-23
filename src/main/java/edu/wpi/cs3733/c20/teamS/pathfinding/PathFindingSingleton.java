package edu.wpi.cs3733.c20.teamS.pathfinding;

public class PathFindingSingleton {

    private static PathFindingSingleton pathFindingSingleton;
    private IPathfinding algorithm;

    private PathFindingSingleton(){}

    private static class SingletonHelper{

        private static final PathFindingSingleton path = new PathFindingSingleton();
    }

    private static PathFindingSingleton getPathType(){
        return SingletonHelper.path;
    }


}
