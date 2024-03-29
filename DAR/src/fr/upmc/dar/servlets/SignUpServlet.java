package fr.upmc.dar.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import fr.upmc.dar.dao.DAOFactory;
import fr.upmc.dar.dao.interfaces.IUserDao;
import fr.upmc.dar.entities.User;
import fr.upmc.dar.enums.UriMapping;
import fr.upmc.dar.tools.SignUpValidator;




@WebServlet(urlPatterns = "/signup" )
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private static final String SINUP_JSP  = "/JSP/signup.jsp";
	
	
	private IUserDao user ;
	protected SignUpValidator validator;
	protected  Map<String,String> formErrors;
	protected JSONObject errors;
	
	
	
	@Override
	public void init() throws ServletException {
		super.init();
		formErrors = new HashMap<String,String>();
		user = DAOFactory.createUserDao();
		validator = new SignUpValidator(user );
		errors = new JSONObject();
	}
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		request.getRequestDispatcher(UriMapping.REGISTER.getRessourceUrl()).forward(request, response);
		
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 String name = request.getParameter("name");
		 String userName = request.getParameter("username");
		 String eMail = request.getParameter("email");
		 String password = request.getParameter("password");
		 String confirmationPassword = request.getParameter("confirmpassword");
		 String etablissement = request.getParameter("university");
		 String cursus = request.getParameter("cursus");
//		 System.out.println("received from form : \n name :"+name
//				 										+"username: "+userName
//				 										
//				 										+"email:"+eMail
//				 										+"pass :"+password
//				 										+"confpass:"+confirmationPassword
//				 										+"etablissement:"+etablissement
//				 										+"cursus :"+cursus);
		 try{
			 validator.canRegisterUser(name, userName, eMail, password, confirmationPassword, etablissement, cursus);
			 formErrors=validator.getCommittedErrors();
			 if(formErrors.isEmpty()){
				 User utilisateur = new User(name,userName,eMail,password,etablissement,cursus);
			
				 user.createUser(utilisateur);
				 response.getWriter().write("welcome new user :"+userName);
				 
			}
		 
			
			else 
				{
					errors.putAll(formErrors);
					
					request.setAttribute("formErrors", formErrors);
					getServletContext().getRequestDispatcher(UriMapping.REGISTER.getRessourceUrl()).forward(request, response);
					System.out.println(errors.toJSONString());
				}
				//response.getWriter().write("l'emeil deja pris");
		 }catch(Exception e){
			 e.printStackTrace();
		 }

	}

}
