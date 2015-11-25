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
    private sensor_msgs.Range ros_range;
    private std_msgs.Header ros_header;
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
        ros_range = publisher.newMessage();
        ros_range.setRadiationType((byte) 0);
        ros_range.setFieldOfView((float) 5.0);
        ros_range.setMaxRange((float) 100.0);
        ros_range.setMinRange((float) 0.5);
        ros_header = ros_range.getHeader();
        ros_header.setFrameId("sonar");        
    }

    public void setDepth(final float rangeData)
    {
        if (publisher == null) {
            Logger.getLogger(RosPosePublisher.class.getName()).log(Level.WARNING, "Publisher null pointer");
        }
        else {
            ros_header.setSeq(sequence);
            ros_header.setStamp(node_master.getCurrentTime());
            // ros_range.setHeader(ros_header);
            
            ros_range.setRange(rangeData);
            
            sequence = sequence+1;
            publisher.publish(ros_range);
        }
    }
}