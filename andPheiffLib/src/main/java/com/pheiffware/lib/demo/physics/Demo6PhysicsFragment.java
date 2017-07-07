package com.pheiffware.lib.demo.physics;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pheiffware.lib.demo.physics.testScenarios.BouncingBall;
import com.pheiffware.lib.demo.physics.testScenarios.CompressedStackedObjects;
import com.pheiffware.lib.demo.physics.testScenarios.ConstrainedStackedObjects;
import com.pheiffware.lib.demo.physics.testScenarios.ConstrainedStackedObjectsDrop;
import com.pheiffware.lib.demo.physics.testScenarios.Elevator;
import com.pheiffware.lib.demo.physics.testScenarios.ElevatorWithLoad;
import com.pheiffware.lib.demo.physics.testScenarios.GeneralScenario1;
import com.pheiffware.lib.demo.physics.testScenarios.GeneralScenario2;
import com.pheiffware.lib.demo.physics.testScenarios.PolygonScenario;
import com.pheiffware.lib.demo.physics.testScenarios.PoolScenario;
import com.pheiffware.lib.demo.physics.testScenarios.SingleBallOnRamp;
import com.pheiffware.lib.demo.physics.testScenarios.SingleBallSitGround;
import com.pheiffware.lib.demo.physics.testScenarios.StackedObjects;
import com.pheiffware.lib.physics.entity.Entity;
import com.pheiffware.lib.simulation.SimulationRunner;

import java.util.List;

/**
 * Example of the physics code and simulation engine running in Android
 * <p/>
 * Created by Steve on 4/2/2016.
 */
public class Demo6PhysicsFragment extends Fragment
{
    private TestPhysicsView testPhysicsView;
    private SimulationRunner<List<Entity>> simulationRunner;
    private TestPhysicsScenario[] physicsScenarios;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        int defaultNumSteps = 3000;
        // @formatter:off
        physicsScenarios = new TestPhysicsScenario[]
                {new PolygonScenario(5.0f, defaultNumSteps), new StackedObjects(3.0f, defaultNumSteps, 40.5f, 500.5f, 20, 5, 800, 0.9f),
                        new CompressedStackedObjects(8.0f, 2000, 40.5f, 500.5f, 20, 5, 800, 0.9f, 2500.0f, 300, 50),
                        new ConstrainedStackedObjectsDrop(5.0f, defaultNumSteps, 40.5f, 500.5f, 20, 8, 800, 0.9f),
                        new ConstrainedStackedObjects(3.0f, defaultNumSteps, 40.5f, 500.5f, 20, 7, 800, 0.9f),
                        new PoolScenario(1.5f, defaultNumSteps, 40, 500, 20, 5, 0.9f), new ElevatorWithLoad(1.5f, defaultNumSteps), new BouncingBall(1.0f, defaultNumSteps),
                        new Elevator(1.0f, defaultNumSteps), new SingleBallSitGround(), new GeneralScenario1(3.0f, defaultNumSteps), new GeneralScenario2(8.0f, defaultNumSteps),
                        new SingleBallOnRamp(3.0f, defaultNumSteps)};
        // @formatter:on

        simulationRunner = new TestPhysicsMultiSimulationRunner(1.0, true, physicsScenarios);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        testPhysicsView = new TestPhysicsView(getContext(), 16, new RectF(0, 0, 800, 800), simulationRunner);
        return testPhysicsView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        simulationRunner.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        simulationRunner.stop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        testPhysicsView = null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        simulationRunner = null;
        physicsScenarios = null;
    }
}
