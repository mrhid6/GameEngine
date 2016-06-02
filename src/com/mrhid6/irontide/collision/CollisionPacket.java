package com.mrhid6.irontide.collision;

import org.lwjgl.util.vector.Vector3f;

import com.mrhid6.irontide.utils.Maths;

public class CollisionPacket {

	private Vector3f eRadius;
	
	private Vector3f R3Velocity;
	private Vector3f R3Position;
	
	private Vector3f velocity;
	private Vector3f normalizedVelocity;
	private Vector3f basePoint;
	
	private boolean foundCollision;
	private double nearestDistance;
	private Vector3f intersectionPoint;
	private Vector3f finalPos;
	
	public CollisionPacket(Vector3f position, Vector3f radius, Vector3f velocity) {
		this.R3Position = position;
		this.R3Velocity = velocity;
		this.eRadius = radius;
		
		this.basePoint = new Vector3f(R3Position.x / eRadius.x, R3Position.y / eRadius.y, R3Position.z / eRadius.z);
		this.velocity = new Vector3f(R3Velocity.x / eRadius.x, R3Velocity.y / eRadius.y, R3Velocity.z / eRadius.z);
		this.normalizedVelocity = new Vector3f(this.velocity.x, this.velocity.y, this.velocity.z);
		this.normalizedVelocity.normalise();
	}
	
	public Vector3f getInitialVelocity() {
		return R3Velocity;
	}
	
	public Vector3f getERadius() {
		return eRadius;
	}
	
	public Vector3f getNewR3Velocity(Vector3f newPosition) {
		return Vector3f.sub(toR3(newPosition), R3Position, null);
	}
	
	public Vector3f toR3(Vector3f eSpace) {
		Vector3f toR3 = new Vector3f();
		toR3.x = eSpace.x * eRadius.x;
		toR3.y = eSpace.y * eRadius.y;
		toR3.z = eSpace.z * eRadius.z;
		return toR3;
	}
	
	public double getNearestDistance() {
		return nearestDistance;
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}
	
	public Vector3f getBasePoint() {
		return basePoint;
	}
	
	public Vector3f getFinalPos() {
		return finalPos;
	}
	
	public Vector3f getIntersectionPoint() {
		return intersectionPoint;
	}
	
	public void setFoundCollision(boolean foundCollision) {
		this.foundCollision = foundCollision;
	}
	
	public boolean foundCollision() {
		return foundCollision;
	}
	
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
	
	public void setNormalisedVelocity(Vector3f velocity) {
		this.normalizedVelocity = velocity;
		this.normalizedVelocity.normalise();
	}
	
	public void setBasePoint(Vector3f basePoint) {
		this.basePoint = basePoint;
	}
	
	public void setFinalPos(Vector3f finalPos) {
		this.finalPos = finalPos;
	}
	
	public void setIntersectionPoint(Vector3f intersectionPoint) {
		this.intersectionPoint = intersectionPoint;
	}
	
	public void setNearestDistance(float nearestDistance) {
		this.nearestDistance = nearestDistance;
	}
	
	public void checkTriangle(Vector3f[] points) {
		Plane trianglePlane = new Plane(points[0], points[1], points[2]);
		
		if (trianglePlane.isFrontFacingTo(this.normalizedVelocity)) {
			float t0;
			float t1;
			boolean embeddedInPlane = false;
			
			float signedDistToTrianglePlane = (float) trianglePlane.signedDistanceTo(this.basePoint);
			
			float normalDotVelocity = Vector3f.dot(trianglePlane.getNormal(), this.velocity);
			
			if (Math.abs(normalDotVelocity) == 0.0f) {
				if (Math.abs(signedDistToTrianglePlane) >= 1.0f) {
					return;
				} else {
					embeddedInPlane = true;
					t0 = 0.0f;
					t1 = 1.0f;
				}
			} else {
				t0 = (-1.0f - signedDistToTrianglePlane) / normalDotVelocity;
				t1 = ( 1.0f - signedDistToTrianglePlane) / normalDotVelocity;
				
				if (t0 > t1) {
					float temp = t1;
					t1 = t0;
					t0 = temp;
				}
				
				if (t0 > 1.0f || t1 < 0.0f) {
					return;
				}
				
				t0 = Maths.clampf(t0, 0.0f, 1.0f);
				t1 = Maths.clampf(t1, 0.0f, 1.0f);
			}
			
			float t = 1.0f;
			Vector3f collisionPoint = null;
			boolean foundCollision = false;
			
			if (!embeddedInPlane) {
				Vector3f v1 = Vector3f.sub(this.basePoint, trianglePlane.getNormal(), null);
				Vector3f v2 = new Vector3f((float) (this.velocity.x * t0), (float) (this.velocity.y * t0), (float) (this.velocity.z * t0));
				Vector3f planeintersectionPoint = Vector3f.add(v1, v2, null);
				
				if (Maths.pointInTriangle(planeintersectionPoint, points)) {
					foundCollision = true;
					t = (float) t0;
					collisionPoint = planeintersectionPoint;
				}
			}
			
			if (!foundCollision) {
				Vector3f velocity = this.velocity;
				Vector3f base = this.basePoint;
				float velocitySquaredLength = velocity.lengthSquared();
				float a, b, c;
				float newT;
				
				a = velocitySquaredLength;
				
				b = 2.0f * Vector3f.dot(velocity, Vector3f.sub(base, points[0], null));
				c = (Vector3f.sub(points[0], base, null)).lengthSquared() - 1.0f;
				newT = Maths.getLowestRoot(a, b, c, t);
				if (newT >= 0) {
					t = newT;
					foundCollision = true;
					collisionPoint = points[0];
				}
				
				b = 2.0f * Vector3f.dot(velocity, Vector3f.sub(base, points[1], null));
				c = (Vector3f.sub(points[1], base, null)).lengthSquared() - 1.0f;
				newT = Maths.getLowestRoot(a, b, c, t);
				if (newT >= 0) {
					t = newT;
					foundCollision = true;
					collisionPoint = points[1];
				}
				
				b = 2.0f * Vector3f.dot(velocity, Vector3f.sub(base, points[2], null));
				c = (Vector3f.sub(points[2], base, null)).lengthSquared() - 1.0f;
				newT = Maths.getLowestRoot(a, b, c, t);
				if (newT >= 0) {
					t = newT;
					foundCollision = true;
					collisionPoint = points[2];
				}
				
				Vector3f edge = Vector3f.sub(points[1], points[0], null);
				Vector3f baseToVertex = Vector3f.sub(points[0], base, null);
				float edgeSquareLength = edge.lengthSquared();
				float edgeDotVelocity = Vector3f.dot(edge, velocity);
				float edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				
				a = edgeSquareLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquareLength * (2 * Vector3f.dot(velocity, baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquareLength * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
				
				newT = Maths.getLowestRoot(a, b, c, t);
				if (newT >= 0) {
					float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquareLength;
					if (f >= 0 && f <= 1) {
						t = newT;
						foundCollision = true;
						collisionPoint = Vector3f.add(points[0], new Vector3f(edge.x * f, edge.y * f, edge.z * f), null);
					}
				}
				
				edge = Vector3f.sub(points[2], points[1], null);
				baseToVertex = Vector3f.sub(points[1], base, null);
				edgeSquareLength = edge.lengthSquared();
				edgeDotVelocity = Vector3f.dot(edge, velocity);
				edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				
				a = edgeSquareLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquareLength * (2 * Vector3f.dot(velocity, baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquareLength * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
				
				newT = Maths.getLowestRoot(a, b, c, t);
				if (newT >= 0) {
					float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquareLength;
					if (f >= 0 && f <= 1) {
						t = newT;
						foundCollision = true;
						collisionPoint = Vector3f.add(points[1], new Vector3f(edge.x * f, edge.y * f, edge.z * f), null);
					}
				}
				
				edge = Vector3f.sub(points[0], points[2], null);
				baseToVertex = Vector3f.sub(points[2], base, null);
				edgeSquareLength = edge.lengthSquared();
				edgeDotVelocity = Vector3f.dot(edge, velocity);
				edgeDotBaseToVertex = Vector3f.dot(edge, baseToVertex);
				
				a = edgeSquareLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquareLength * (2 * Vector3f.dot(velocity, baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquareLength * (1 - baseToVertex.lengthSquared()) + edgeDotBaseToVertex * edgeDotBaseToVertex;
				
				newT = Maths.getLowestRoot(a, b, c, t);
				if (newT >= 0) {
					float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquareLength;
					if (f >= 0 && f <= 1) {
						t = newT;
						foundCollision = true;
						collisionPoint = Vector3f.add(points[2], new Vector3f(edge.x * f, edge.y * f, edge.z * f), null);
					}
				}
			}
			
			if (foundCollision) {
				float distToCollision = t * this.velocity.length();
				System.out.println(distToCollision);
				
				if (!this.foundCollision || distToCollision < this.nearestDistance) {
					this.nearestDistance = distToCollision;
					this.intersectionPoint = collisionPoint;
					this.foundCollision = true;
				}
			}
		}
	}
	
	public void checkTriangle(Triangle triangle) {
		checkTriangle(triangle.getVertices());
	}
}
