package com.pheiffware.andpheifflib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.pheiffware.andpheifflib.sphere.view.TestPhysicsView;
import com.pheiffware.andpheifflib.testing.physics.TestPhysicsScenario;
import com.pheiffware.andpheifflib.testing.physics.TestingPhysicsSystemManager;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.BouncingBall;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.CompressedStackedObjects;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.ConstrainedStackedObjects;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.ConstrainedStackedObjectsDrop;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.Elevator;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.ElevatorWithLoad;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.GeneralScenario1;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.GeneralScenario2;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.PolygonScenario;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.PoolScenario;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.SingleBallOnRamp;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.SingleBallSitGround;
import com.pheiffware.andpheifflib.testing.physics.testScenarios.StackedObjects;

public class PhysicsActivity extends AppCompatActivity {
    private TestPhysicsView testView;
    private TestingPhysicsSystemManager physicsSystemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Debug.startMethodTracing("sphere");
        //@formatter:off
        TestPhysicsScenario[] physicsScenarios = new TestPhysicsScenario[]
                {
                        new PolygonScenario(5.0f),
                        new StackedObjects(3.0f, 40.5f, 500.5f, 20, 5, 800, 0.9f),
                        new CompressedStackedObjects(8.0f, 40.5f, 500.5f, 20, 5, 800,
                                0.9f, 2500.0f, 300, 50),
                        new ConstrainedStackedObjectsDrop(5.0f, 40.5f, 500.5f, 20, 8,
                                800, 0.9f),
                        new ConstrainedStackedObjects(3.0f, 40.5f, 500.5f, 20, 7, 800,
                                0.9f), new PoolScenario(1.5f, 40, 500, 20, 5, 0.9f),
                        new ElevatorWithLoad(1.5f), new BouncingBall(1.0f),
                        new Elevator(1.0f), new SingleBallSitGround(),
                        new GeneralScenario1(3.0f), new GeneralScenario2(8.0f),
                        new SingleBallOnRamp(3.0f) };
        //@formatter:on
        physicsSystemManager = new TestingPhysicsSystemManager(0.0002f, 0.1f,
                true, physicsScenarios);

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
