package com.froggengo.practise.importselector;


public class HelloServiceImpl {

    String name;
    String world;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void say(){
        System.out.println(name+" : "+world);
    }


}
