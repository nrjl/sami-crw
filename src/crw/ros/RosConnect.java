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
    private RosDepthSensorPublisher pubNodeDepth;
    private RosTempSensorPublisher pubNodeTemp;
    private RosWaypointReached pubNodeWaypointReached;
    private RosWaypointSubscriber subNodeWaypoint;
    private RosClearWaypointsSubscriber subNodeWaypointClear;
    
    private class NodeObject{
        public NodeMain node;
        public NodeConfiguration config;
        
        public NodeObject(NodeMain node, NodeConfiguration config){
            this.node = node;
            this.config = config;
        }
    }
    
    private NodeObject GetNodeLoader(String pubArg) {
        String[] pubArgv = new String[] {pubArg};
        CommandLineLoader pubLoader = new CommandLineLoader(Lists.newArrayList(pubArgv));
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Loading node class: " + pubLoader.getNodeClassName());
        NodeMain nodeOut = null;
        try {
            nodeOut = pubLoader.loadClass(pubLoader.getNodeClassName());
        }
        catch (ClassNotFoundException e) {
            throw new RosRuntimeException("Unable to locate node: " + pubLoader.getNodeClassName(), e);
        }
        catch (InstantiationException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + pubLoader.getNodeClassName(), e);
        }
        catch (IllegalAccessException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + pubLoader.getNodeClassName(), e);
        }
        assert(nodeOut != null);
        NodeObject nodeObject = new NodeObject(nodeOut, pubLoader.build());
        return nodeObject;
    }

    public void ConnectPubSubs(BoatProxy parent_boat) throws Exception {
        // Set up the executor for nodes
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        
        // Load GeoPose publisher
        NodeObject nodePose = GetNodeLoader("crw.ros.RosGeoPosePublisher");
        pubNodePose = (RosGeoPosePublisher) nodePose.node;
        nodeMainExecutor.execute(pubNodePose, nodePose.config);
        
        // Load Depth Sensor publisher
        NodeObject nodeDepth = GetNodeLoader("crw.ros.RosDepthSensorPublisher");
        pubNodeDepth = (RosDepthSensorPublisher) nodeDepth.node;
        nodeMainExecutor.execute(pubNodeDepth, nodeDepth.config);
        
        // Load Temperature sensor publisher
        NodeObject nodeTemp = GetNodeLoader("crw.ros.RosTempSensorPublisher");
        pubNodeTemp = (RosTempSensorPublisher) nodeTemp.node;
        nodeMainExecutor.execute(pubNodeTemp, nodeTemp.config);        
        
        // Load WaypointReached publisher
        NodeObject nodeWaypointReached = GetNodeLoader("crw.ros.RosWaypointReached");
        pubNodeWaypointReached = (RosWaypointReached) nodeWaypointReached.node;
        nodeMainExecutor.execute(pubNodeWaypointReached, nodeWaypointReached.config);

        // Load the waypoint subscriber
        NodeObject nodeWaypoint = GetNodeLoader("crw.ros.RosWaypointSubscriber");
        subNodeWaypoint = (RosWaypointSubscriber) nodeWaypoint.node;
        nodeMainExecutor.execute(subNodeWaypoint, nodeWaypoint.config);

        // This allows passing the parent boat to the subscriber so that 
        // messages can be sent to the boat on receiving a ROS waypoint          
        subNodeWaypoint.setBoatParent(parent_boat);                
        
        //load the waypoint clear subscriber
        NodeObject nodeClear = GetNodeLoader("crw.ros.RosClearWaypointsSubscriber");
        subNodeWaypointClear = (RosClearWaypointsSubscriber) nodeClear.node;
        nodeMainExecutor.execute(subNodeWaypointClear, nodeClear.config);
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
    
    public void setDepth(final float depth) {
        assert(pubNodeDepth != null);
        pubNodeDepth.setDepth(depth);
    }
    
    public void setTemp(final float temp) {
        assert(pubNodeTemp != null);
        pubNodeTemp.setTemp(temp);
    }
}


/*
        // Load GeoPose publisher
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
        

        String[] subArgv = { "crw.ros.RosWaypointSubscriber" };
        CommandLineLoader subLoader = new CommandLineLoader(Lists.newArrayList(subArgv));
        String nodeClassName = subLoader.getNodeClassName();
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

*/