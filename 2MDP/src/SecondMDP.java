
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
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;

public class SecondMDP {

	DomainGenerator dg;
    Domain domain;
    State initState;
    RewardFunction rf;
    TerminalFunction tf;
    SimpleHashableStateFactory hashFactory;
    int numStates;
    public SecondMDP(double p1, double p2)
    {
    	this.numStates = 6;
    	this.dg = new GraphDefinedDomain(numStates);
    	//s0
    	((GraphDefinedDomain) this.dg).setTransition(0, 0, 0, p1);
    	((GraphDefinedDomain) this.dg).setTransition(0, 0, 1, 1.-p1);
    	((GraphDefinedDomain) this.dg).setTransition(0, 1, 2, 1.);
    	//s1
    	((GraphDefinedDomain) this.dg).setTransition(1, 0, 3, 1. - p2);
    	((GraphDefinedDomain) this.dg).setTransition(1, 0, 5, p2);
    	((GraphDefinedDomain) this.dg).setTransition(1, 1, 4, 1.);
    	//s2
    	((GraphDefinedDomain) this.dg).setTransition(2, 0, 1, 1.);
    	//s3
    	((GraphDefinedDomain) this.dg).setTransition(3, 0, 1, 1.);
    	//s4
    	((GraphDefinedDomain) this.dg).setTransition(4, 0, 5, 1.);
    	//s5
    	
    	this.domain = this.dg.generateDomain();
    	this.initState = GraphDefinedDomain.getState(this.domain, 0);
    	this.rf = new FourParamRF(p1,p2);
    	this.tf = new SingleStateTF(5);
    	this.hashFactory = new SimpleHashableStateFactory();
    }
    
    public static class SingleStateTF implements TerminalFunction
    {
    	int terminalSid;
    	public SingleStateTF(int sid)
    	{
    		this.terminalSid = sid;
    	}
    	@Override
    	public boolean isTerminal(State s)
    	{
    		int sid = GraphDefinedDomain.getNodeId(s);
    		return sid == this.terminalSid;
    	}
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
		
		public FourParamRF(double p1, double p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
		
		@Override
        // TODO:
		// Override the reward method to match the reward scheme from the state diagram.
		// See the documentation for the RewardFunction interface for the proper signature.
		// You may find the getNodeId method from GraphDefinedDomain class helpful.
		public double reward(State s, GroundedAction a, State sprime){
			int sid = GraphDefinedDomain.getNodeId(s);
			int spid = GraphDefinedDomain.getNodeId(sprime);
		    //System.out.println("sid: " + sid + "spid " + spid);
			if (sid == 0 && spid ==1)
		       return 3;
		    else if (sid == 0 && spid == 0)
		       return -1;
		    else if (sid == 0 && spid == 2)
		       return 1;
		    else if (sid== 1 && spid == 3)
		       return 1;
		    else if (sid== 1 && spid == 4)
			       return 2;
		    else if (sid== 1 && spid == 5)
		    	return 0;
		    else if (sid== 2)
			       return 0;
		    else if (sid== 3 )
			       return 0;
		    else if (sid == 4)
		       return 0;
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
	    Policy p = new GreedyQPolicy(vi);
	    State s0 = GraphDefinedDomain.getState(this.domain, 0);
	    State s1 = GraphDefinedDomain.getState(this.domain, 1);
	    System.out.println(p.getAction(s0).actionName());
	    String s ="";
	    if ((p.getAction(s0).actionName()).compareTo("action0") == 0)
	    	s += "a,";
	    if (p.getAction(s0).actionName().compareTo("action1") == 0)
	    	s += "b, ";
	    if (p.getAction(s1).actionName().compareTo("action0") == 0)
	    	s+="c";
	    if (p.getAction(s1).actionName().compareTo("action1") == 0)
	    	s+="d";
	    return s;
	}
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double p1 = 0.4;
		double p2 = 0.1;
		SecondMDP mdp = new SecondMDP(p1,p2);
		
		double gamma = 0.6;
		System.out.println("Best initial action: " + mdp.bestFirstAction(gamma));
        
        
	}

}
