package ro.fortsoft.pippo.demo.webjars;

import ro.fortsoft.pippo.core.Pippo;
import ro.fortsoft.pippo.core.Request;
import ro.fortsoft.pippo.core.Response;
import ro.fortsoft.pippo.core.route.RouteHandler;
import ro.fortsoft.pippo.core.route.RouteHandlerChain;

/**
 * @author Decebal Suiu
 */
public class WebjarsDemo {

    public static void main(String[] args) {
        Pippo pippo = new Pippo();
        pippo.getApplication().GET("/", new RouteHandler() {

            @Override
            public void handle(Request request, Response response, RouteHandlerChain chain) {
                response.render("webjars.ftl");
            }

        });
        /*
        pippo.getApplication().GET("/webjars/*", new RouteHandler() {

            @Override
            public void handle(Request request, Response response, RouteHandlerChain chain) {

            }

        });
        */
        pippo.start();
    }

}
