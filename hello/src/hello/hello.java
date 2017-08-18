package hello;

import java.lang.*;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.core.*;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

public class hello {

	DomainGenerator dg;
    Domain domain;
    State initState;
    RewardFunction rf;
    TerminalFunction tf;
    SimpleHashableStateFactory hashFactory;
    int numStates;
    public hello(double p1, double p2, double p3, double p4)
    {
    	this.numStates = 6;
    	this.dg = new GraphDefinedDomain(numStates);
    	((GraphDefinedDomain) this.dg).setTransition(0, 0, 1, 1.);
    	((GraphDefinedDomain) this.dg).setTransition(0, 1, 2, 1.);
    	((GraphDefinedDomain) this.dg).setTransition(0, 2, 3, 1.);
    	
    	((GraphDefinedDomain) this.dg).setTransition(1, 0, 1, 1.);  
    	((GraphDefinedDomain) this.dg).setTransition(2, 0, 4, 1.);
    	((GraphDefinedDomain) this.dg).setTransition(3, 0, 5, 1.);
    	((GraphDefinedDomain) this.dg).setTransition(4, 0, 2, 1.);
    	((GraphDefinedDomain) this.dg).setTransition(5, 0, 5, 1.);
    	this.domain = this.dg.generateDomain();
    	this.initState = GraphDefinedDomain.getState(this.domain, 0);
    	this.rf = new FourParamRF(p1,p2,p3,p4);
    	this.tf = new NullTermination();
    	this.hashFactory = new SimpleHashableStateFactory();
    }
    
    private ValueIteration computeValue(double gamma){
       double maxDelta = 0.0001;
       int maxIteration = 1000;
       ValueIteration vi = new ValueIteration(this.domain, this.rf, this.tf, gamma, this.hashFactory, maxDelta, maxIteration );
       vi.planFromState(this.initState);
       return vi;
    }
    
    public Domain getDomain() {
        return this.domain;
    }
    
    public static class FourParamRF implements RewardFunction {
		double p1;
		double p2;
		double p3;
		double p4;
		
		public FourParamRF(double p1, double p2, double p3, double p4) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
		}
		
		@Override
        // TODO:
		// Override the reward method to match the reward scheme from the state diagram.
		// See the documentation for the RewardFunction interface for the proper signature.
		// You may find the getNodeId method from GraphDefinedDomain class helpful.
		public double reward(State s, GroundedAction a, State sprime){
			int sid = GraphDefinedDomain.getNodeId(s);
		    if (sid == 1)
		       return this.p1;
		    else if (sid == 2)
		       return this.p2;
		    else if (sid == 0 || sid == 3)
		       return 0;
		    else if (sid== 4)
		       return this.p3;
		    else if (sid == 5)
		       return this.p4;
		    else
		    	throw new RuntimeException("Error sid" + sid);
		}
    }
    
    public String bestFirstAction(double gamma) {
		// Return "action a" if a is the best action based on the discount factor given.
		// Return "action b" if b is the best action based on the discount factor given.
		// Return "action c" if c is the best action based on the discount factor given.
		// If there is a tie between actions, give preference to the earlier action in the alphabet:
		//   e.g., if action a and action c are equally good, return "action a".
	
	    ValueIteration vi = computeValue(gamma);
	    double []v = new double[this.numStates]; 
	    for (int i = 0; i < this.numStates; i++)
	    {
	        State s = GraphDefinedDomain.getState(this.domain, i);
	        System.out.println(vi.value(s));
	        v[i] = vi.value(s);
	    } 
	    String action = null;
	    if (v[1] > v[2] && v[1] > v[3]) action = "action a";
	    else if (v[2] > v[1] && v[2] > v[3]) action = "action b";
	    else if (v[3] > v[1] && v[3] > v[2]) action = "action c";
	    
	    return action;
	}
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double p1 = 5.;
		double p2 = 6.;
		double p3 = 3.;
		double p4 = 7.;
		hello mdp = new hello(p1,p2,p3,p4);
		
		double gamma = 0.6;
		System.out.println("Best initial action: " + mdp.bestFirstAction(gamma));
        
        
	}

}
