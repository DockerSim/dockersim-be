package com.dockersim.service.image;

import com.dockersim.domain.DockerImage;
import com.dockersim.domain.ImageLocation;
import com.dockersim.domain.Simulation;
import com.dockersim.repository.DockerImageRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DockerImageFinderImpl implements DockerImageFinder {

    private final DockerImageRepository repo;

    @Override
    public List<DockerImage> findImages(String imageNameOrId, Simulation simulation) {
        // Search for images by both name and ID to handle ambiguity.

        // 1. Search by name
        List<DockerImage> byName = findImagesByName(imageNameOrId, simulation);

        // 2. Search by ID (if the string could be a hex ID)
        List<DockerImage> byId = new ArrayList<>();
        if (isPotentialId(imageNameOrId)) {
            repo.findByImageIdAndSimulation(imageNameOrId, simulation)
                .ifPresent(byId::add);
            // If full ID doesn't match, try partial ID
            if (byId.isEmpty()) {
                byId.addAll(
                    repo.findAllByImageIdStartingWithAndSimulation(imageNameOrId, simulation));
            }
        }

        // 3. Combine results, ensuring no duplicates
        return Stream.concat(byName.stream(), byId.stream()).distinct().toList();
    }

    @Override
    public List<DockerImage> getImages(Simulation simulation, ImageLocation location) {
        return repo.findAllBySimulationAndLocation(simulation, location);
    }

    private List<DockerImage> findImagesByName(String name, Simulation simulation) {
        Map<String, String> parsed = parserImageName(name);
        String repository = parsed.get("repository");
        String tag = parsed.get("tag");

        // A tag was specified if the last ':' comes after the last '/'
        boolean tagWasSpecified = name.lastIndexOf(':') > name.lastIndexOf('/');

        if (tagWasSpecified) {
            // If a specific tag is given, search for that exact image
            return repo.findByNameAndTagAndSimulation(repository, tag, simulation)
                .map(List::of).orElse(Collections.emptyList());
        } else {
            // If no tag is given (e.g., "ubuntu"), it's ambiguous.
            // Return all images with that name and let the service layer handle it.
            return repo.findAllByNameAndSimulation(repository, simulation);
        }
    }


    @Override
    public Map<String, String> parserImageName(String fullName) {
        Map<String, String> map = new HashMap<>();
        String repository = fullName;
        String tag = "latest";

        int lastColon = fullName.lastIndexOf(':');
        if (lastColon > 0) {
            int lastSlash = fullName.lastIndexOf('/');
            if (lastSlash < lastColon) {
                repository = fullName.substring(0, lastColon);
                tag = fullName.substring(lastColon + 1);
            }
        }
        map.put("repository", repository);
        map.put("tag", tag);
        return map;
    }

    private boolean isPotentialId(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        // A potential ID is a hex string.
        return str.matches("^[0-9a-fA-F]+$");
    }
}
