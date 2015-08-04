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
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.coords.UTMCoord;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nick
 */
public class RosWaypointReached extends AbstractNodeMain {

    private Publisher<geographic_msgs.GeoPose> publisher;
    private geographic_msgs.GeoPoint ros_geopoint ;
    private geometry_msgs.Quaternion ros_quat;
    private geographic_msgs.GeoPose ros_geopose;
    

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("crw_ros/crw_waypoint_reached");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher("crw_waypoint_reached", geographic_msgs.GeoPose._TYPE);
        ros_geopose = publisher.newMessage();        
        ros_geopoint = ros_geopose.getPosition();
        ros_quat  = ros_geopose.getOrientation();
    }

    public void setPose(final UtmPose newpose)
    {
        if (publisher == null) {
            Logger.getLogger(RosPosePublisher.class.getName()).log(Level.WARNING, "Publisher null pointer");
        }
        else {
            int longZone = newpose.origin.zone;

            // Convert hemisphere to arbitrary worldwind codes
            // Notice that there is a "typo" in South that exists in the WorldWind code
            // String wwHemi = (_pose.origin.isNorth) ? "gov.nasa.worldwind.avkey.North" : "gov.nasa.worldwdind.avkey.South";
            String wwHemi = (newpose.origin.isNorth) ? AVKey.NORTH : AVKey.SOUTH;

            // Fill in UTM data structure
            UTMCoord boatPos = UTMCoord.fromUTM(longZone, wwHemi, newpose.pose.getX(), newpose.pose.getY());
            
            ros_geopoint.setLatitude(boatPos.getLatitude().degrees);
            ros_geopoint.setLongitude(boatPos.getLongitude().degrees);
            ros_geopoint.setAltitude(0);

            final robotutils.Quaternion newpose_quat = newpose.pose.getRotation();
            ros_quat.setX(newpose_quat.getX());
            ros_quat.setY(newpose_quat.getY());
            ros_quat.setZ(newpose_quat.getZ());
            ros_quat.setW(newpose_quat.getW());

            ros_geopose.setPosition(ros_geopoint);
            ros_geopose.setOrientation(ros_quat);

            publisher.publish(ros_geopose);
        }
    }
}
