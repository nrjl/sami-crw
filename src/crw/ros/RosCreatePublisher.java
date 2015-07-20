/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crw.ros;

import com.google.common.collect.Lists;
import edu.cmu.ri.crw.data.UtmPose;

import org.ros.exception.RosRuntimeException;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RosCreatePublisher {

    private RosPosePublisher pubNodeMain;

    public void SpawnPublisher() throws Exception {
        // Set up the executor for nodes
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();

        // Load the publisher
        String[] pubArgv = { "crw.ros.RosPosePublisher" };
        CommandLineLoader pubLoader = new CommandLineLoader(Lists.newArrayList(pubArgv));
        String nodeClassName = pubLoader.getNodeClassName();
        Logger.getLogger(RosCreatePublisher.class.getName()).log(Level.INFO, "Loading node class: " + pubLoader.getNodeClassName());
        NodeConfiguration pubNodeConfiguration = pubLoader.build();

        pubNodeMain = null;
        try {
            pubNodeMain = (RosPosePublisher)pubLoader.loadClass(nodeClassName);
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

        assert(pubNodeMain != null);
        nodeMainExecutor.execute(pubNodeMain, pubNodeConfiguration);
    }

    public void setPose(final UtmPose newpose) {
        assert(pubNodeMain != null);
        pubNodeMain.setPose(newpose);
    }
}
