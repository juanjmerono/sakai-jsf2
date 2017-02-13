package demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.app.roster.Participant;
import org.sakaiproject.api.app.roster.RosterManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;

public class RosterBean {
	
	private static Log log = LogFactory.getLog(RosterBean.class);
	
	protected String group;
	public String getGroup() { return group; }
	public void setGroup(String group) { this.group = group; } 

	protected RosterManager rosterManager;
	public void setRosterManager(RosterManager rosterManager) { this.rosterManager = rosterManager; }
	
	protected SiteService siteService;
	public void setSiteService(SiteService siteService) { this.siteService = siteService; }
	
	protected ToolManager toolManager;
	public void setToolManager(ToolManager toolManager) { this.toolManager = toolManager; }
	
	public List<Participant> getDataModel() {
		List<Participant> list = new ArrayList<Participant>();
		if (rosterManager!=null) list = group==null||group.equals("-")?rosterManager.getRoster():rosterManager.getRoster(group);
		return list;
	}
	
    public List<SelectItem> getGroupOptions() {
    	List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(new SelectItem("-","-- Group --"));
		if (siteService!=null && toolManager!=null) {
	    	try {
	    		Collection<Group> groups = siteService.getSite(toolManager.getCurrentPlacement().getContext()).getGroups();
	    		for (Group g:groups) {
	    			items.add(new SelectItem(g.getId(),g.getTitle()));
	    		}
	    	} catch (IdUnusedException ex) {
	    		log.error("Error getting groups.",ex);
	    	}
		}
        return items;  
    }  	
    
    public void save(ActionEvent actionEvent) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		context.addMessage(null, new FacesMessage("Successful", "Hello " + group));
		context.addMessage(null, new FacesMessage("Second Message", "Additional Info Here..."));
	}    
    
}
