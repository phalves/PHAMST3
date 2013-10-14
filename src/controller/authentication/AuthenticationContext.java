package controller.authentication;

import view.panels.AuthenticatedState0;
import view.panels.AuthenticatedState1;
import view.panels.AuthenticatedState2;
import view.panels.AuthenticatedState3;

public class AuthenticationContext {

	private AuthenticationState currentState;
	private AuthenticationStateID authenticationStateID;
	
	public AuthenticationContext() {
		currentState = new AuthenticatedState0();
		authenticationStateID = AuthenticationStateID.AuthState0;
	}
	
	public AuthenticationState getCurrentState() {
		return currentState;
	}

	public AuthenticationState getAuthenticationState() {
		return currentState;
	}
	
	public void nextState() {
		switch (authenticationStateID) {
			case AuthState0:
				currentState = new AuthenticatedState1();
				authenticationStateID = AuthenticationStateID.AuthState1;
				
				break;
				
			case AuthState1:
				currentState = new AuthenticatedState2();
				authenticationStateID = AuthenticationStateID.AuthState2;
				
				break;
				
			case AuthState2:
				currentState = new AuthenticatedState3();
				authenticationStateID = AuthenticationStateID.AuthState3;
				
				break;
				
			case AuthState3:
				
				break;
		}
			
	}
	
	private enum AuthenticationStateID {
		AuthState0, //Unauthenticated
		AuthState1, //Logged In
		AuthState2, //Entered Password
		AuthState3  //Authenticated
	}
}
