import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import entity.Job;
import entity.OneTimeJob;
import entity.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class JobTest extends MultithreadedTestCase {

    private Job job1;

    private Job job2;

    private Job job3;

    @Override
    public void initialize() {
        job1 = new OneTimeJob("jobType1",1);
        job2 = new OneTimeJob("jobType2", 0);
        job3 = new OneTimeJob("jobType3", 2);
    }

    public void thread1() throws InterruptedException {
        job1.run();
    }

    public void thread2() throws InterruptedException {
        job2.run();
    }

    public void thread3() throws InterruptedException {
        job3.run();
    }

    @Override
    public void finish() {
        Assertions.assertEquals(State.FINISHED, job1.getState());
        Assertions.assertEquals(State.FINISHED, job2.getState());
        Assertions.assertEquals(State.FINISHED, job3.getState());
    }

    @Test
    public void testCounter() throws Throwable {
        TestFramework.runManyTimes(new JobTest(), 50);
    }
}