package service.implementation.nps;

import repository.interfaces.NPSScores;
import service.interfaces.frontend.NPSService;

public class NPSServiceImpl implements NPSService {
    private final NPSScores npsScores;

    public NPSServiceImpl(NPSScores npsScores) {
        this.npsScores = npsScores;
    }

    // NPS = % promoters (score 9-10) minus % critics (score 0-6)
    @Override
    public float getNPS() {
        int total = npsScores.countByScoreRange(0,10);
        if (total == 0) return 0.0f;
        return getPromoters(total)-getCritics(total);
    }

    private float getPromoters(int total){
        return ((float) npsScores.countByScoreRange(9, 10) /total)*100;
    }
    private float getCritics(int total){
        return ((float) npsScores.countByScoreRange(0, 6) /total)*100;
    }
}