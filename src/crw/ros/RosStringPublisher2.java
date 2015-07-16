/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crw.ros;

import com.google.common.base.Preconditions;

import crw.proxy.BoatProxy;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import edu.cmu.ri.crw.data.UtmPose;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nick
 */
public class RosStringPublisher2 extends AbstractNodeMain {

    private static RosStringPublisher2 instance;
    private Publisher<std_msgs.String> publisher;

    private boolean initialized = false;
    
    private RosStringPublisher2() {}

    public static RosStringPublisher2 getInstance() {
        if (instance == null) {
            instance = new RosStringPublisher2();
            Logger.getLogger(BoatProxy.class.getName()).log(Level.INFO, "Position ROS publisher created.");
        }
        return instance;
    }

    public boolean isInitialized() {
        return initialized;
    }
        
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava/crw_string_pub");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        Preconditions.checkArgument(! initialized, "ROS position publisher already initialized");
        
        publisher = connectedNode.newPublisher("crw_string_pub", std_msgs.String._TYPE);
        publisher.setLatchMode(true);
        
        std_msgs.String str = publisher.newMessage();
        str.setData("Opening message");

        for(int i=1; i<11; i++) {
            try {
                publisher.publish(str);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        Logger.getLogger(BoatProxy.class.getName()).log(Level.INFO, "ROS position publisher initialized.");
    }

    @Override
    public void onShutdown(Node node) {
        Preconditions.checkArgument(initialized, "ROS position publisher not initialized");

        publisher.shutdown();
        initialized = false;
        Logger.getLogger(BoatProxy.class.getName()).log(Level.INFO, "ROS position publisher shutdown");
    }
    
    public void setPose(final UtmPose newpose)
    {
        if (publisher == null ) {
            Logger.getLogger(BoatProxy.class.getName()).log(Level.WARNING, "Publisher null pointer");
        }
        else {
          std_msgs.String str = publisher.newMessage();
          str.setData("TEST");
          publisher.publish(str);
        }
    }
}
