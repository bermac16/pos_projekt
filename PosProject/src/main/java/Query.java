
import com.ibm.watson.assistant.v1.model.MessageResponse;
import com.ibm.watson.assistant.v1.model.RuntimeEntity;
import java.time.LocalDate;
import java.util.LinkedList;


public class Query {
    protected MessageResponse response;
    private LocalDate startDate, endDate;

    public Query(MessageResponse messageResponse) throws UnknownQueryException {
        this.response = messageResponse;
        parseTimeRange();
    }
    
    private void parseTimeRange() throws UnknownQueryException {
        LinkedList<LocalDate> dates = new LinkedList<>();
        
        for (RuntimeEntity entity : response.getEntities()) {
            if(entity.getEntity().equals(Entity.date.getName())) {
                dates.add(LocalDate.parse(entity.getValue()));
            }
        }
        
        if(dates.size() != 2)
            throw new UnknownQueryException("wrong number of dates");
        
        dates.sort(new LocalDateComparator());
        
        startDate = dates.get(0);
        endDate = dates.get(1);
    }
}
