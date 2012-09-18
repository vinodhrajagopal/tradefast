package junit;

import java.util.List;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.test.Helpers;
import play.db.ebean.Model;
import play.test.FakeApplication;

import com.avaje.ebean.Ebean;

public class ModelTest<T extends Model> {

  public static FakeApplication app;

  @BeforeClass
  public static void startApp() {
    app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
    Helpers.start(app);
  }

  @Before
  public void beforeEachTest() {
    Ebean.save(fixturesToLoad());
  }

  @After
  public void afterEachTest() {
    Ebean.delete(fixturesToUnload());
  }

  // template methods to load/unload fixtures
  public List<T> fixturesToLoad()   { return new ArrayList<T>(); }
  public List<T> fixturesToUnload() { return new ArrayList<T>();}

  @AfterClass
  public static void stopApp() {
    Helpers.stop(app);
  }

}