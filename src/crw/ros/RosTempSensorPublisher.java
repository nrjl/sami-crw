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
public class RosTempSensorPublisher extends AbstractNodeMain {

    private Publisher<sensor_msgs.Temperature> publisher; 
    private sensor_msgs.Temperature temp_msg;
    private std_msgs.Header header;
    private ConnectedNode node_master;
    private static int sequence=0;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("crw_ros/crw_temp_pub");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher("crw_temp_pub", sensor_msgs.Temperature._TYPE);
        node_master = connectedNode;
        temp_msg = publisher.newMessage();
        temp_msg.setVariance( 0.04);
        header = temp_msg.getHeader();
        header.setFrameId("sonar");  
    }

    public void setTemp(final float tempData)
    {
        if (publisher == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Publisher null pointer");
        }
        else {
            header.setSeq(sequence);
            header.setStamp(node_master.getCurrentTime());
            // temp_msg.setHeader(header);
            
            temp_msg.setTemperature(tempData);
            
            sequence = sequence+1;
            publisher.publish(temp_msg);
        }
    }
}