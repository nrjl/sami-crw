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
import edu.cmu.ri.crw.data.UtmPose;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nick
 */
public class RosPosePublisher extends AbstractNodeMain {

    private Publisher<geometry_msgs.Pose> publisher;
    private geometry_msgs.Pose ros_pose ;
    private geometry_msgs.Point ros_point;
    private geometry_msgs.Quaternion ros_quat;
    

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("crw_ros/crw_pose_pub");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher("crw_pose_pub", geometry_msgs.Pose._TYPE);
        ros_pose = publisher.newMessage();
        
        ros_point = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
        ros_quat  = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
    }

    public void setPose(final UtmPose newpose)
    {
        if (publisher == null) {
            Logger.getLogger(RosPosePublisher.class.getName()).log(Level.WARNING, "Publisher null pointer");
        }
        else {          
          final double[] newpose_point = newpose.pose.getPosition();
          final robotutils.Quaternion newpose_quat = newpose.pose.getRotation();
          ros_point.setX(newpose_point[0]);
          ros_point.setY(newpose_point[1]);
          ros_point.setZ(newpose_point[2]);
          ros_quat.setX(newpose_quat.getX());
          ros_quat.setY(newpose_quat.getY());
          ros_quat.setZ(newpose_quat.getZ());
          ros_quat.setW(newpose_quat.getW());
                    
          ros_pose.setPosition(ros_point);
          ros_pose.setOrientation(ros_quat);
          
          publisher.publish(ros_pose);
        }
    }
}