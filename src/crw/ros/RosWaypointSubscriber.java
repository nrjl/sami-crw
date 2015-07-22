/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crw.ros;

import crw.event.output.proxy.ProxyGotoPoint;
import crw.proxy.BoatProxy;
import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;
import sami.path.Location;
import sami.path.UTMCoordinate;
import sami.proxy.ProxyInt;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nick
 */

public class RosWaypointSubscriber extends AbstractNodeMain {

    private Subscriber<geographic_msgs.GeoPose> subscriber;
    private geographic_msgs.GeoPoint ros_geopoint ;
    private geographic_msgs.GeoPose ros_geopose;
    private BoatProxy parent_boat;
    private final static Object LOCK = new Object();
    
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("crw_ros/crw_waypoint_sub");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        parent_boat = null;
        synchronized (LOCK) {
        subscriber = connectedNode.newSubscriber("crw_waypoint_sub", geographic_msgs.GeoPose._TYPE);
        //now that subscriber is no longer 'null' exit the while loop    
         LOCK.notifyAll();
        }
        ros_geopose = connectedNode.getTopicMessageFactory().newFromType(geographic_msgs.GeoPose._TYPE);
        ros_geopoint = ros_geopose.getPosition();
    }

    public void setBoatParent(final BoatProxy parent_boat) {
    //synchronization allows the program to wait while the subscriber is instantiated to continue
     synchronized (LOCK) {
      while (subscriber == null) {
       try {
           LOCK.wait();
         } catch (InterruptedException x) {
          break;}
         }
       }
       
      /*while(subscriber == null) {
        Logger.getLogger(RosWaypointSubscriber.class.getName()).log(Level.INFO, "Waiting for subsriber to link.");}
       */

        this.parent_boat = parent_boat;
        subscriber.addMessageListener(new MessageListener<geographic_msgs.GeoPose>() {
        @Override
        public void onNewMessage(geographic_msgs.GeoPose waypoint) {

            if (parent_boat == null) {
                Logger.getLogger(RosWaypointSubscriber.class.getName()).log(Level.WARNING, "No parent boat assigned, waypoint ignored!");
            }
            else {
                updateWaypoint(waypoint);
            }
        }
        });
    }
    
    public void updateWaypoint(final geographic_msgs.GeoPose waypoint) {
        Location target_point = new Location();
        ros_geopoint = waypoint.getPosition();

        target_point.setAltitude(ros_geopoint.getAltitude());
        UTMCoordinate crw_utm = new UTMCoordinate(ros_geopoint.getLatitude(), ros_geopoint.getLongitude());
        Logger.getLogger(RosWaypointSubscriber.class.getName()).log(Level.INFO, "Sending new waypoint: " + crw_utm.toString());

        target_point.setCoordinate(crw_utm);
        ProxyGotoPoint waypoint_event = new ProxyGotoPoint();
        Hashtable<ProxyInt, Location> proxy_table = new Hashtable<ProxyInt, Location>();
        proxy_table.put(parent_boat, target_point);
        waypoint_event.setProxyPoints(proxy_table);
        parent_boat.handleEvent(waypoint_event, null);
    }
    
    public Object getLOCK(){
    return LOCK;}
}