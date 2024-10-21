package inkball;

import processing.core.PVector;

class Line {
    PVector start;
    PVector end;

    Line(PVector start, PVector end) {
        this.start = start;
        this.end = end;
    }

    // // Method to calculate the distance from a ball to the line segment
    // public float distanceToBall(float ballX, float ballY) {
    //     PVector ballPos = new PVector(ballX, ballY);
    //     PVector lineDir = PVector.sub(end, start);
    //     float t = PVector.sub(ballPos, start).dot(lineDir) / lineDir.magSq();
    //     t = constrain(t, 0, 1);  // Ensure t is within [0, 1] to stay within the line segment
    //     PVector closestPoint = PVector.add(start, PVector.mult(lineDir, t));
    //     return PVector.dist(ballPos, closestPoint);  // Return the distance from the ball to the closest point on the line
    // }

    // // Check if the ball collides with the line
    // public boolean checkCollision(Ball ball) {
    //     float ballRadius = ball.getRadius();
    //     float distance = distanceToBall(ball.posX, ball.posY);  // Calculate the distance to the ball
    //     return distance <= ballRadius;  // A collision occurs if the ball's radius intersects with the line
    // }

    // // Calculate the new velocity of the ball after a collision with the line
    // public PVector calculateNewVelocity(Ball ball) {
    //     PVector ballVelocity = new PVector((float) ball.velocityX, (float) ball.velocityY);

    //     // Calculate the normal of the line segment
    //     PVector segment = PVector.sub(end, start);
    //     PVector normal = new PVector(-segment.y, segment.x).normalize();  // Normal vector for reflection

    //     // Calculate reflection using the formula: v' = v - 2 * (v ⋅ n) * n
    //     float dotProduct = PVector.dot(ballVelocity, normal);
    //     PVector newVelocity = PVector.sub(ballVelocity, PVector.mult(normal, 2 * dotProduct));

    //     return newVelocity;
    // }

    // // Utility method to constrain a value within a range (0, 1)
    // private float constrain(float value, float min, float max) {
    //     return Math.max(min, Math.min(value, max));
    // }
}
