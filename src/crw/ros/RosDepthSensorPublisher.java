/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crw.ros;


import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nick
 */
public class RosDepthSensorPublisher extends AbstractNodeMain {

    private Publisher<sensor_msgs.Range> publisher; 
    private sensor_msgs.Range range_msg;
    private std_msgs.Header header;
    private ConnectedNode node_master;
    private static int sequence=0;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("crw_ros/crw_sonar_pub");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher("crw_sonar_pub", sensor_msgs.Range._TYPE);
        node_master = connectedNode;
        range_msg = publisher.newMessage();
        range_msg.setRadiationType((byte) 0);
        range_msg.setFieldOfView((float) 5.0);
        range_msg.setMaxRange((float) 100.0);
        range_msg.setMinRange((float) 0.5);
        header = range_msg.getHeader();
        header.setFrameId("sonar");        
    }

    public void setDepth(final float rangeData)
    {
        if (publisher == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Publisher null pointer");
        }
        else {
            header.setSeq(sequence);
            header.setStamp(node_master.getCurrentTime());
            // range_msg.setHeader(header);
            
            range_msg.setRange(rangeData);
            
            sequence = sequence+1;
            publisher.publish(range_msg);
        }
    }
}