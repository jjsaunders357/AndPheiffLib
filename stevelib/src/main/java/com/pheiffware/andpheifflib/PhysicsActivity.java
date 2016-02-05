package com.pheiffware.andpheifflib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

import com.pheiffware.andpheifflib.sphere.engine.physics.testing.PhysicsScenario;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.TestingPhysicsSystemManager;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.BouncingBall;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.CompressedStackedObjects;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.ConstrainedStackedObjects;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.ConstrainedStackedObjectsDrop;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.Elevator;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.ElevatorWithLoad;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.GeneralScenario1;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.GeneralScenario2;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.PolygonPointScenario;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.PolygonScenario;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.PoolScenario;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.SingleBallOnRamp;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.SingleBallSitGround;
import com.pheiffware.andpheifflib.sphere.engine.physics.testing.scenario.StackedObjects;
import com.pheiffware.andpheifflib.sphere.view.TestPhysicsView;

public class PhysicsActivity extends AppCompatActivity {
    private TestPhysicsView testView;
    private TestingPhysicsSystemManager physicsSystemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Debug.startMethodTracing("sphere");
        //@formatter:off
        PhysicsScenario[] physicsScenarios = new PhysicsScenario[] {
                new ConstrainedStackedObjectsDrop(7.5f, 40.5f, 500.5f, 20, 8, 800, 0.9f),
                new ConstrainedStackedObjects(20.0f, 40.5f, 500.5f, 20, 7, 800, 0.9f),
                new StackedObjects(3.0f, 40.5f, 500.5f, 20, 5, 800, 0.9f),
                new CompressedStackedObjects(8.0f, 40.5f, 500.5f, 20, 5, 800, 0.9f, 2500.0f, 300, 50),
                new PoolScenario(1.5f, 40, 500, 20, 5, (float) Math.sqrt(0.94)),
                new PolygonScenario(10.0f),
                new PolygonPointScenario(2.0f),
                new ElevatorWithLoad(1.5f),
                new BouncingBall(1.0f),
                new Elevator(1.0f),
                new SingleBallSitGround(),
                new GeneralScenario1(3.0f),
                new GeneralScenario2(8.0f),
                new SingleBallOnRamp(3.0f)
        };
        //@formatter:on
        physicsSystemManager = new TestingPhysicsSystemManager(0.002f, 0.1f,
                true, physicsScenarios, 12345);

        testView = new TestPhysicsView(this, physicsSystemManager);
        setContentView(R.layout.activity_physics);
        LinearLayout rootView = (LinearLayout) findViewById(R.id.root);
        rootView.addView(testView, 0);

        physicsSystemManager.start();
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Debug.stopMethodTracing();
        physicsSystemManager.stop();
    }
}
