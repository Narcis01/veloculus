package com.miele.backend.migration;


import com.miele.backend.team.Team;

import java.io.IOException;

public interface MigrationData {
    void exportTeam() throws IOException;
    void exportDev(Team team) throws IOException;
    void exportSprints(String teamName) throws  IOException;
    void importSprint(Team team) throws IOException;
    void importDev(Team team) throws IOException;
    void importTeam() throws IOException;
}
