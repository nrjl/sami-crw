/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crw.ros;

import crw.event.output.proxy.ProxyEmergencyAbort;
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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sami.event.OutputEvent;
/**
 *
 * @author rdml
 */
public class RosClearWaypointsSubscriber extends AbstractNodeMain {
    
    private Subscriber<std_msgs.String> subscriber;
    private BoatProxy parent_boat;
    private final static Object LOCK = new Object();

    
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("crw_ros/clear_waypoints");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        parent_boat = null;
        synchronized (LOCK) {
        subscriber = connectedNode.newSubscriber("clear_waypoints", std_msgs.String._TYPE);
        //now that subscriber is no longer 'null' exit the while loop    
        LOCK.notifyAll();
        }
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
       
      /* while(subscriber == null) {
        Logger.getLogger(RosClearWaypointsSubscriber.class.getName()).log(Level.INFO, "Waiting for subsriber to link.");}
       */

        this.parent_boat = parent_boat;

         subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
        @Override
         public void onNewMessage(std_msgs.String message) {

            if (parent_boat == null) {
                Logger.getLogger(RosClearWaypointsSubscriber.class.getName()).log(Level.WARNING, "No parent boat assigned, waypoint ignored!");
            }
            else if(message.getData().equalsIgnoreCase("clear")) {
                updateWaypoint();
                //displaying the 'clear' message in the logger for verification
                Logger.getLogger(RosClearWaypointsSubscriber.class.getName()).log(Level.INFO, "Message Recieved: " + message.getData());
            }
            else if(message.getData().equalsIgnoreCase("abort")) {
                ProxyEmergencyAbort waypoint_event = new ProxyEmergencyAbort();
                parent_boat.handleEvent(waypoint_event, null);
                //displaying the 'clear' message in the logger for verification
                Logger.getLogger(RosClearWaypointsSubscriber.class.getName()).log(Level.INFO, "Message Recieved: " + message.getData());
            }
        }
        });
    }
    
    public void updateWaypoint() {
    //Abort the current Output Event, for waypoint following this OE is ProxyGotoPoint 
        OutputEvent remove = parent_boat.getCurrentEvent();
        if (remove == null) {
            Logger.getLogger(RosClearWaypointsSubscriber.class.getName()).log(Level.INFO, "No current events found, nothing to clear.");
        }
        else {
            Logger.getLogger(RosClearWaypointsSubscriber.class.getName()).log(Level.INFO, "Clearing current event of type: " + remove.toString());
            UUID id = remove.getId();
            parent_boat.abortEvent(id);
        }
        
    }
}
