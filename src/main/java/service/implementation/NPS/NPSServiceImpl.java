package service.implementation.NPS;

import repository.interfaces.NPSScores;
import service.interfaces.frontend.NPSService;

public class NPSServiceImpl implements NPSService {
    private final NPSScores npsScores;

    public NPSServiceImpl(NPSScores npsScores) {
        this.npsScores = npsScores;
    }

    @Override
    public float getNPS() {
        return getPromoters()-getCritics();
    }

    private float getPromoters(){
        return ((float) npsScores.countByScoreRange(9, 10) /npsScores.countByScoreRange(0,10))*100;
    }
    private float getCritics(){
        return ((float) npsScores.countByScoreRange(0, 6) /npsScores.countByScoreRange(0,10))*100;
    }
}
