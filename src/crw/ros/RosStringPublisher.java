/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crw.ros;


import crw.proxy.BoatProxy;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import edu.cmu.ri.crw.data.UtmPose;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nick
 */
public class RosStringPublisher extends AbstractNodeMain {

    private Publisher<std_msgs.String> publisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava/crw_string_pub");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher("crw_string_pub", std_msgs.String._TYPE);

        std_msgs.String str = publisher.newMessage();
        str.setData("Opening message");

        for(int i=1; i<11; i++) {
            try {
                publisher.publish(str);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }

    }

    public void setPose(final UtmPose newpose)
    {
        if (publisher == null) {
            Logger.getLogger(BoatProxy.class.getName()).log(Level.WARNING, "Publisher null pointer");
        }
        else {
          std_msgs.String str = publisher.newMessage();
          str.setData(newpose.toString());
          publisher.publish(str);
        }
    }
}
