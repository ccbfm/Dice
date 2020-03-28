package com.ccbfm.dice.stereoscopic;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Vector3f;

public class SubstanceGenerator {

    /**
     * 初始化物理世界的方法
     */
    public static DiscreteDynamicsWorld createWorld() {
        //创建碰撞检测配置信息对象
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        //创建碰撞检测算法分配者对象，其功能为扫描所有的碰撞检测对，并确定适用的检测策略对应的算法
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        //设置整个物理世界的边界信息
        Vector3f worldBorderMin = new Vector3f(-1, 0, -1);
        Vector3f worldBorderMax = new Vector3f(1, 1, 1);
        int maxProxies = 1024;
        //创建碰撞检测粗测阶段的加速算法对象
        AxisSweep3 overlappingPairCache = new AxisSweep3(worldBorderMin, worldBorderMax, maxProxies);
        //创建推动约束解决者对象
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        //创建物理世界对象
        DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
        //设置重力加速度
        dynamicsWorld.setGravity(new Vector3f(0, -60, 0));

        //上下
        CollisionShape planeShape1 = new StaticPlaneShape(new Vector3f(1, 0, 0), -6);
        createStaticRigidBody(planeShape1, dynamicsWorld);
        CollisionShape planeShape2 = new StaticPlaneShape(new Vector3f(-1, 0, 0), -6);
        createStaticRigidBody(planeShape2, dynamicsWorld);
        //左右
        CollisionShape planeShape3 = new StaticPlaneShape(new Vector3f(0, 0, 1), -10);
        createStaticRigidBody(planeShape3, dynamicsWorld);
        CollisionShape planeShape4 = new StaticPlaneShape(new Vector3f(0, 0, -1), -10);
        createStaticRigidBody(planeShape4, dynamicsWorld);
        //深浅
        CollisionShape planeShape5 = new StaticPlaneShape(new Vector3f(0, -1, 0), -15);
        createStaticRigidBody(planeShape5, dynamicsWorld);
        CollisionShape planeShape6 = new StaticPlaneShape(new Vector3f(0, 1, 0), 0);
        createStaticRigidBody(planeShape6, dynamicsWorld);

        return dynamicsWorld;
    }

    public static Floor createFloor(int programId, int textureId) {
        return new Floor(programId, textureId);
    }

    public static Dice createDice(RigidBody rigidBody, float[][] data, int programId, int textureId) {
        Dice dice = new Dice(rigidBody);
        dice.initFloatBuffer(data[0], data[1], data[2]);
        dice.initShader(programId, textureId);
        return dice;
    }

    public static void createStaticRigidBody(CollisionShape groundShape, DiscreteDynamicsWorld dynamicsWorld) {
        //创建刚体的初始变换对象
        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(new Vector3f(0.f, 0.f, 0.f));
        Vector3f localInertia = new Vector3f(0, 0, 0);//惯性
        //创建刚体的运动状态对象
        DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
        //创建刚体信息对象
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, myMotionState, groundShape, localInertia);
        //创建刚体
        RigidBody body = new RigidBody(rbInfo);
        //设置反弹系数
        body.setRestitution(0.3f);
        //设置摩擦系数
        body.setFriction(2.0f);
        //将刚体添加进物理世界
        dynamicsWorld.addRigidBody(body);
    }

    public static RigidBody createDynamicRigidBody(CollisionShape colShape,
                                                   DiscreteDynamicsWorld dynamicsWorld, float mass,
                                                   float cx, float cy, float cz) {
        boolean isDynamic = (mass != 0f);//物体是否可以运动
        Vector3f localInertia = new Vector3f(0, 0, 0);//惯性向量
        //如果物体可以运动
        if (isDynamic) {
            colShape.calculateLocalInertia(mass, localInertia);//计算惯性
        }
        Transform startTransform = new Transform();//创建刚体的初始变换对象
        startTransform.setIdentity();//变换初始化
        startTransform.origin.set(new Vector3f(cx, cy, cz));//设置初始的位置
        //创建刚体的运动状态对象
        DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
        //创建刚体信息对象
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo
                (mass, myMotionState, colShape, localInertia);
        RigidBody body = new RigidBody(rbInfo);//创建刚体
        body.setRestitution(0.3f);//设置反弹系数
        body.setFriction(2.0f);//设置摩擦系数

        dynamicsWorld.addRigidBody(body);//将刚体添加进物理世界
        return body;
    }
}
