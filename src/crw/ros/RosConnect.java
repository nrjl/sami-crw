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
    private RosWaypointSubscriber subNodeWaypoint;

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
    }

    public void setPose(final UtmPose newpose) {
        assert(pubNodePose != null);
        pubNodePose.setPose(newpose);
    }
    
}
