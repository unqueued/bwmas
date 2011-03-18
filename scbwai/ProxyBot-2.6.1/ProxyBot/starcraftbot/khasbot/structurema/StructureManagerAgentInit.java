
/* StructureManager initiator 
	class StructureManagerAgentInit extends AchieveREInitiator{
		
		public StructureManagerAgentInit(Agent a, ACLMessage msg) {
			super(a, msg);
		}
		protected void handleInform(ACLMessage inform){
			System.out.println("Agent responded!!!");
		}
		protected void handleRefuse(ACLMessage refuse) {
			System.out.println("Agent "+refuse.getSender().getName()+" refused to perform the requested action");
		}
		protected void handleFailure(ACLMessage failure){
			System.out.println("Failed to get response!!!");
		}
		//protected void handleAllResultNotifications(Vector notifications) {
			//if (notifications.size() < nResponders) {
				// Some responder didn't reply within the specified timeout
				System.out.println("Timeout expired: missing " + (notifications.size()) + " responses");
			//}
		//}
	}//end StructureManagerAgentInit
  */


