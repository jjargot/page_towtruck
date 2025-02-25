import groovy.lang.Binding

import java.util.List
import java.util.Map

import org.codehaus.groovy.control.CompilerConfiguration;

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Runtime;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Runtime;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;

import org.apache.commons.lang3.StringEscapeUtils

import org.bonitasoft.engine.identity.User;
import org.bonitasoft.console.common.server.page.PageContext
import org.bonitasoft.console.common.server.page.PageController
import org.bonitasoft.console.common.server.page.PageResourceProvider
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;

import com.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.web.extension.rest.RestAPIContext
import org.bonitasoft.web.extension.rest.RestApiController
import org.bonitasoft.web.extension.ResourceProvider

import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.log.event.BEventFactory
import org.bonitasoft.log.event.BEvent

import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.api.TenantAdministrationAPI;
import org.bonitasoft.engine.api.ThemeAPI;
import org.bonitasoft.engine.api.BusinessDataAPI;
import org.bonitasoft.engine.api.PageAPI;
import org.bonitasoft.engine.api.PermissionAPI;
import org.bonitasoft.engine.api.ApplicationAPI;

import org.bonitasoft.engine.command.CommandDescriptor;
import org.bonitasoft.engine.command.CommandCriterion;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;

import com.bonitasoft.custompage.towtruck.Timer.MethodResetTimer;
import com.bonitasoft.custompage.towtruck.Timer;

import com.bonitasoft.custompage.towtruck.groovymaintenance.GroovyMaintenance;


public class Actions {
  
  private static Logger logger = Logger.getLogger("org.bonitasoft.custompage.towtruck.groovy");  
  
  
  public final static String GROOVY_REST_API_CONTEXT = "restAPIContext";
  public final static String GROOVY_API_ACCESSOR = "apiAccessor";
  public final static String GROOVY_API_CLIENT = "apiClient";
  
  

  /**
   * build the apiAccessor access
   *
   */
  public static class MyApiAccessor
  {
    APISession session;

    public ProcessAPI getProcessAPI() {
      logger.info("CALL GetprocessAPI()");
      return TenantAPIAccessor.getProcessAPI(session);
    }

    public IdentityAPI getIdentityAPI() {
      return TenantAPIAccessor.getIdentityAPI(session);
    }
    public CommandAPI getCommandAPI() {
      return TenantAPIAccessor.getCommandAPI(session);
    }
    public BusinessDataAPI getBusinessDataAPI(){
      return TenantAPIAccessor.getBusinessDataAPI(session);
    }
    public PageAPI getCustomPageAPI() {
      return TenantAPIAccessor.getCustomPageAPI(session);
    }
    public ApplicationAPI getLivingApplicationAPI() {
      return TenantAPIAccessor.getLivingApplicationAPI(session);
    }
    public LoginAPI getLoginAPI() {
      return TenantAPIAccessor.getLoginAPI(session);
    }
    public ProfileAPI getProfileAPI()    {
      return TenantAPIAccessor.getProfileAPI(session);
    }
    public TenantAdministrationAPI getTenantAdministrationAPI() {
      return TenantAPIAccessor.getTenantAdministrationAPI(session);
    }
    public ThemeAPI getThemeAPI() {
      return TenantAPIAccessor.getThemeAPI(session);
    }
    public refresh() {
      TenantAPIAccessor.refresh();
    }
  }
  
  /**
   * build the APIClient access
   *
   */
  public static class MyAPIClient extends APIClient
  {
    APISession session;

    public ProcessAPI getProcessAPI() {
      logger.info("CALL MyAPIClient.GetprocessAPI()");
      return TenantAPIAccessor.getProcessAPI(session);
    }
    public IdentityAPI getIdentityAPI() {
      return TenantAPIAccessor.getIdentityAPI(session);
    }
    public CommandAPI getCommandAPI() {
      return TenantAPIAccessor.getCommandAPI(session);
    }
    public BusinessDataAPI getBusinessDataAPI(){
      return TenantAPIAccessor.getBusinessDataAPI(session);
    }
    public PageAPI getCustomPageAPI() {
      return TenantAPIAccessor.getCustomPageAPI(session);
    }
    public PermissionAPI getPermissionAPI() {
      return null; // does not exist
    }
    public ProfileAPI getProfileAPI()  {
      return TenantAPIAccessor.getProfileAPI(session);
    }
    public APISession getSession() {
      return session;
    }
    public TenantAdministrationAPI getTenantAdministrationAPI() {
      return TenantAPIAccessor.getTenantAdministrationAPI(session);
    }
    public ThemeAPI getThemeAPI() {
      return TenantAPIAccessor.getThemeAPI(session);      
    }
  }

  /**
   * build the RestAPIContext access
   *
   */
  public static class MyRestContext implements RestAPIContext 
  {
    public MyAPIClient myApiClient;
    public Locale locale;
    public PageResourceProvider resourceProvider;
    
    public APIClient getApiClient()
    {
      logger.info("CALL MyRestContext.getApiClient()");      
      return myApiClient;
    }
    public APISession getApiSession()
    { return myApiClient.session; }

    public Locale getLocale() 
    {
      return locale;
    }

    public ResourceProvider getResourceProvider()
    {
      return resourceProvider;
    }
  }

  
  
  
  
  public static Index.ActionAnswer doAction(HttpServletRequest request, String paramJsonSt, HttpServletResponse response, PageResourceProvider pageResourceProvider, PageContext pageContext) {

    logger.info("#### towtruckCustomPage:Actions start");
    Index.ActionAnswer actionAnswer = new Index.ActionAnswer();
    try {
      String action = request.getParameter("action");
      logger.info("#### towtruckCustomPage:Actions  action is[" + action + "] !");
      if (action == null || action.length() == 0) {
        actionAnswer.isManaged = false;
        logger.info("#### towtruckCustomPage:Actions END No Actions");
        return actionAnswer;
      }
      actionAnswer.isManaged = true;

      HttpSession httpSession = request.getSession();
      APISession session = pageContext.getApiSession();
      ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(session);
      IdentityAPI identityApi = TenantAPIAccessor.getIdentityAPI(session);
      CommandAPI commandAPI = TenantAPIAccessor.getCommandAPI(session);
      File pageDirectory = pageResourceProvider.getPageDirectory();
      
      if ("getmissingtimer".equals(action)) {
        actionAnswer.setResponse(Timer.getMissingTimers(false, processAPI));
      } else if ("createmissingtimers".equals(action)) {
        String typecreation = request.getParameter("typecreation");
        MethodResetTimer methodResetTimer = null;
        if ("handle".equals(typecreation))
          methodResetTimer = MethodResetTimer.Handle;
        else if ("recreate".equals(typecreation))
          methodResetTimer = MethodResetTimer.Recreate;
        else if ("retrytask".equals(typecreation))
          methodResetTimer = MethodResetTimer.RetryTask;
        else if ("executeTask".equals(typecreation))
          methodResetTimer = MethodResetTimer.ExecuteTask;

        InputStream is = pageResourceProvider.getResourceAsStream("lib/CustomPageTowTruck-1.0.1.jar");

        actionAnswer.setResponse(Timer.createMissingTimers(methodResetTimer, is, processAPI, commandAPI, null));
      }

      else if ("deletetimers".equals(action)) {
        actionAnswer.setResponse(Timer.deleteTimers(processAPI));
      }
      else if ("groovyload".equals(action)) {
        String groovyCode = request.getParameter("code");
        actionAnswer.responseMap = GroovyMaintenance.getGroovyMaintenance( request, groovyCode, pageDirectory);
      }        
      else if ("groovyexecute".equals(action)) {
        String paramJsonPartial = request.getParameter("paramjson");
        logger.info("collect_add paramJsonPartial=[" + paramJsonPartial + "]");

        String accumulateJson = (String) httpSession.getAttribute("accumulate");
        if (accumulateJson==null)
          accumulateJson="";
        if (paramJsonPartial!=null)
          accumulateJson += paramJsonPartial; //already decode by Tomcat  java.net.URLDecoder.decode(paramJsonPartial, "UTF-8");
        
        
        logger.info("Final Accumulator: accumulateJson=[" + accumulateJson + "]");

        Map<String, Object> groovyParameters = JSONValue.parse(accumulateJson);
        
        // reset the accumulator
        httpSession.setAttribute("accumulate", "");

        
        String groovySrc = null;
        String type = groovyParameters.get("type");
        if (type!=null && ("src".equals( type ) || type.length()==0) ) {
          groovySrc = groovyParameters.get("src");
          logger.info("#### towtruckCustomPage:GroovyExecution directSrc startby[" + (groovySrc.length() > 10 ? groovySrc.substring(0, 8) + "..." : groovySrc) + "]");

        }

        // Actions actions = new Actions();
        try {
          // actionAnswer.responseMap.put("result", actions.executeGroovy(groovySrc, pageResourceProvider, pageContext));
          Binding binding = getBinding( pageResourceProvider,  pageContext);
          actionAnswer.responseMap = GroovyMaintenance.executeGroovyMaintenance( request, groovySrc, (List) groovyParameters.getAt("placeholder"), binding );

          
          
        } catch (Exception e) {
          StringWriter sw = new StringWriter();
          PrintWriter pw = new PrintWriter(sw);
          pw.println(e.getMessage());
          e.printStackTrace(pw);
          pw.flush();
          actionAnswer.responseMap.put("exception", sw.toString());
          pw.close();
          sw.close();
        }
      } else if ("groovyrest".equals(action))
      {
        // first, load the code
        String groovyCode = request.getParameter("code");
        actionAnswer.responseMap = GroovyMaintenance.getGroovyMaintenance( request, groovyCode, pageDirectory);
        
        String status = actionAnswer.responseMap.get("status");

        if ("DOWNLOADED".equals(status))
        {          
          // second, execute
          Binding binding = getBinding( pageResourceProvider,  pageContext);
          List<Map<String, Object>> groovyParameters = new ArrayList();
          logger.info("#### towtruckCustomPage:Request.getParametersName="+request.getParameterNames());
             
          for (String parameterName : request.getParameterNames())
          {            
            Map oneParameter = [ "name": parameterName, "value":request.getParameter( parameterName)];
            groovyParameters.add( oneParameter);
            logger.info("#### towtruckCustomPage:Parameter["+parameterName+"] value=["+request.getParameter( parameterName)+"]");
            
          }

          actionAnswer.responseMap = GroovyMaintenance.executeGroovyMaintenance( request, null, (List) groovyParameters, binding );
          
        }
        

      // collect mechanism
      } else if ("collect_add".equals(action)) {
        String paramJsonPartial = request.getParameter("paramjson");
        logger.info("collect_add paramJsonPartial=[" + paramJsonPartial + "]");

        String accumulateJson = (String) httpSession.getAttribute("accumulate");
        if (accumulateJson==null)
          accumulateJson="";
        accumulateJson += paramJsonPartial; // Tomcat already decode java.net.URLDecoder.decode(paramJsonPartial, "UTF-8");
        httpSession.setAttribute("accumulate", accumulateJson);
        actionAnswer.responseMap.put("status", "ok");
      } else {
        actionAnswer.responseMap.put("timerstatus", "Unknow command [" + action + "]");
      }

      logger.info("#### towtruckCustomPage:Actions END responseMap =" + actionAnswer.responseMap.size());
      return actionAnswer;
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      String exceptionDetails = sw.toString();
      logger.severe("#### towtruckCustomPage:Groovy Exception [" + e.toString() + "] at " + exceptionDetails);
      actionAnswer.isResponseMap = true;
      actionAnswer.responseMap.put("Error", "towtruckCustomPage:Groovy Exception [" + e.toString() + "] at " + exceptionDetails);
      return actionAnswer;
    }
  }

  
  public String executeGroovy(String script, PageResourceProvider pageResourceProvider,  PageContext pageContext  ) throws Exception
  {
    MyApiAccessor myApiAccessor = new MyApiAccessor();
    myApiAccessor.session = pageContext.getApiSession();
    MyAPIClient myAPIClient = new MyAPIClient();
    myAPIClient.session = pageContext.getApiSession();

    MyRestContext myRestContext = new MyRestContext();
    myRestContext.myApiClient = myAPIClient;
    myRestContext.locale = pageContext.getLocale();
    myRestContext.resourceProvider = pageResourceProvider;

    Binding binding = new Binding();
    binding.setVariable(GROOVY_REST_API_CONTEXT, myRestContext);
    binding.setVariable(GROOVY_API_ACCESSOR, myApiAccessor );
    binding.setVariable(GROOVY_API_CLIENT, myAPIClient );
    
    // GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding);
    CompilerConfiguration conf = new CompilerConfiguration();
    GroovyShell shell = new GroovyShell(binding, conf);

    String result= shell.evaluate(script);
    // logger.info("#### towtruckCustomPage:Result ="+result);
    if (result==null)
      result="Script was executed with success, but do not return any result."
    return result;
  
  }

  
  private static  Binding getBinding(PageResourceProvider pageResourceProvider, PageContext pageContext)
  {
    MyApiAccessor myApiAccessor = new MyApiAccessor();
    myApiAccessor.session = pageContext.getApiSession();
    MyAPIClient myAPIClient = new MyAPIClient();
    myAPIClient.session = pageContext.getApiSession();
  
    MyRestContext myRestContext = new MyRestContext();
    myRestContext.myApiClient = myAPIClient;
    myRestContext.locale = pageContext.getLocale();
    myRestContext.resourceProvider = pageResourceProvider;
  
    Binding binding = new Binding();
    binding.setVariable(GROOVY_REST_API_CONTEXT, myRestContext);
    binding.setVariable(GROOVY_API_ACCESSOR, myApiAccessor );
    binding.setVariable(GROOVY_API_CLIENT, myAPIClient );
    return binding;
  }
 
  

}
