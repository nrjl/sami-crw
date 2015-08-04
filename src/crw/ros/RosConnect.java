/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crw.ros;

import com.google.common.collect.Lists;
import crw.proxy.BoatProxy;
import edu.cmu.ri.crw.data.UtmPose;

import org.ros.exception.RosRuntimeException;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RosConnect {

    private RosGeoPosePublisher pubNodePose;
    private RosWaypointReached pubNodeWaypointReached;
    private RosWaypointSubscriber subNodeWaypoint;
    private RosClearWaypointsSubscriber subNodeWaypointClear;

    public void ConnectPubSubs(BoatProxy parent_boat) throws Exception {

        // Set up the executor for nodes
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();

        // Load the publisher
        String[] pubArgv = { "crw.ros.RosGeoPosePublisher" };
        CommandLineLoader pubLoader = new CommandLineLoader(Lists.newArrayList(pubArgv));
        String nodeClassName = pubLoader.getNodeClassName();
        Logger.getLogger(RosCreatePublisher.class.getName()).log(Level.INFO, "Loading node class: " + pubLoader.getNodeClassName());
        NodeConfiguration pubNodeConfiguration = pubLoader.build();

        pubNodePose = null;
        try {
            pubNodePose = (RosGeoPosePublisher)pubLoader.loadClass(nodeClassName);
        }
        catch (ClassNotFoundException e) {
            throw new RosRuntimeException("Unable to locate node: " + nodeClassName, e);
        }
        catch (InstantiationException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }
        catch (IllegalAccessException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }

        assert(pubNodePose != null);
        nodeMainExecutor.execute(pubNodePose, pubNodeConfiguration);
        
        // Load the publisher
        String[] pubArgv2 = {"crw.ros.RosWaypointReached"};
        pubLoader = new CommandLineLoader(Lists.newArrayList(pubArgv2));
        nodeClassName = pubLoader.getNodeClassName();
        Logger.getLogger(RosCreatePublisher.class.getName()).log(Level.INFO, "Loading node class: " + pubLoader.getNodeClassName());
        pubNodeConfiguration = pubLoader.build();

        pubNodeWaypointReached = null;
        try {
            pubNodeWaypointReached = (RosWaypointReached)pubLoader.loadClass(nodeClassName);
        }
        catch (ClassNotFoundException e) {
            throw new RosRuntimeException("Unable to locate node: " + nodeClassName, e);
        }
        catch (InstantiationException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }
        catch (IllegalAccessException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }
        assert(pubNodeWaypointReached != null);
        nodeMainExecutor.execute(pubNodeWaypointReached, pubNodeConfiguration);
        

        // Load the subscriber               
        String[] subArgv = { "crw.ros.RosWaypointSubscriber" };
        CommandLineLoader subLoader = new CommandLineLoader(Lists.newArrayList(subArgv));
        nodeClassName = subLoader.getNodeClassName();
        Logger.getLogger(RosCreatePublisher.class.getName()).log(Level.INFO, "Loading node class: " + subLoader.getNodeClassName());
        NodeConfiguration subNodeConfiguration = subLoader.build();

        subNodeWaypoint = null;

        try {
          
         subNodeWaypoint = (RosWaypointSubscriber)subLoader.loadClass(nodeClassName);        
        }
        catch (ClassNotFoundException e) {
            throw new RosRuntimeException("Unable to locate node: " + nodeClassName, e);
        }
        catch (InstantiationException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }
        catch (IllegalAccessException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }
     
          
        assert(subNodeWaypoint != null);

        nodeMainExecutor.execute(subNodeWaypoint, subNodeConfiguration);
        // This allows me to pass the parent boat to the subscriber so that 
        // messages can be sent to the boat on receiving a ROS waypoint          
        subNodeWaypoint.setBoatParent(parent_boat);
        
        
        
        //load the waypoint clear subscriber
        String[] subClearArgv = { "crw.ros.RosClearWaypointsSubscriber" };
        CommandLineLoader subClearLoader = new CommandLineLoader(Lists.newArrayList(subClearArgv));
        nodeClassName = subClearLoader.getNodeClassName();
        Logger.getLogger(RosCreatePublisher.class.getName()).log(Level.INFO, "Loading node class: " + subClearLoader.getNodeClassName());
        NodeConfiguration subClearNodeConfiguration = subClearLoader.build();

        subNodeWaypointClear = null;
        try {
            subNodeWaypointClear = (RosClearWaypointsSubscriber)subClearLoader.loadClass(nodeClassName);

         }
        catch (ClassNotFoundException e) {
            throw new RosRuntimeException("Unable to locate node: " + nodeClassName, e);
        }
        catch (InstantiationException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }
        catch (IllegalAccessException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }

        assert(subNodeWaypointClear != null);
        nodeMainExecutor.execute(subNodeWaypointClear, subClearNodeConfiguration);
        // This allows me to pass the parent boat to the subscriber so that 
        // messages can be sent to the boat on receiving a ROS waypoint        
        subNodeWaypointClear.setBoatParent(parent_boat);
     
    }

    public void setPose(final UtmPose newpose) {
        assert(pubNodePose != null);
        pubNodePose.setPose(newpose);
    }
    
    public void arrivedAtWaypoint(final UtmPose newpose) {
        assert(pubNodeWaypointReached != null);
        pubNodeWaypointReached.setPose(newpose);
    }
}
