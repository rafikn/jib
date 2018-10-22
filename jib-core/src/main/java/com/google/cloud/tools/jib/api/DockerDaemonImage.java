/*
 * Copyright 2018 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.tools.jib.api;
// TODO: Move to com.google.cloud.tools.jib once that package is cleaned up.

import com.google.cloud.tools.jib.builder.BuildSteps;
import com.google.cloud.tools.jib.configuration.BuildConfiguration;
import com.google.cloud.tools.jib.configuration.ImageConfiguration;
import com.google.cloud.tools.jib.docker.DockerClient;
import com.google.cloud.tools.jib.image.ImageReference;
import com.google.cloud.tools.jib.image.InvalidImageReferenceException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

/**
 * Builds to the Docker daemon.
 */
// TODO: Add tests once JibContainerBuilder#containerize() is added.
public class DockerDaemonImage implements TargetImage {

  /**
   * Instantiate with the image reference to tag the built image with. This is the name that shows
   * up on the Docker daemon.
   *
   * @param imageReference the image reference
   * @return a new {@link DockerDaemonImage}
   */
  public static DockerDaemonImage named(ImageReference imageReference) {
    return new DockerDaemonImage(imageReference);
  }

  /**
   * Instantiate with the image reference to tag the built image with. This is the name that shows
   * up on the Docker daemon.
   *
   * @param imageReference the image reference
   * @return a new {@link DockerDaemonImage}
   * @throws InvalidImageReferenceException if {@code imageReference} is not a valid image
   * reference
   */
  public static DockerDaemonImage named(String imageReference)
      throws InvalidImageReferenceException {
    return named(ImageReference.parse(imageReference));
  }

  private final ImageReference imageReference;
  private Path dockerExecutable = Paths.get("docker");
  private Map<String, String> dockerEnvironment = Collections.emptyMap();

  /**
   * Instantiate with {@link #named}.
   */
  private DockerDaemonImage(ImageReference imageReference) {
    this.imageReference = imageReference;
  }

  /**
   * Sets the path to the {@code docker} CLI. This is {@code docker} by default.
   *
   * @param dockerExecutable the path to the {@code docker} CLI
   * @return this
   */
  public DockerDaemonImage setDockerExecutable(Path dockerExecutable) {
    this.dockerExecutable = dockerExecutable;
    return this;
  }

  @Override
  public ImageConfiguration toImageConfiguration() {
    return ImageConfiguration.builder(imageReference).build();
  }

  @Override
  public BuildSteps toBuildSteps(BuildConfiguration buildConfiguration) {
    return BuildSteps.forBuildToDockerDaemon(
        DockerClient.newClient(dockerExecutable, dockerEnvironment), buildConfiguration);
  }

  /**
   * Gets the path to the {@code docker} CLI.
   *
   * @return the path to the {@code docker} CLI
   */
  Path getDockerExecutable() {
    return dockerExecutable;
  }

  /**
   * Gets environment variables for the {@code docker} CLI.
   *
   * @return the path to the {@code docker} CLI
   */
  public Map<String, String> getDockerEnvironment() {
    return dockerEnvironment;
  }

  /**
   * Sets environment variables for the {@code docker} CLI.
   *
   * @param dockerEnvironment a map of docker environment variables
   * 
   */
  public void setDockerEnvironment(Map<String, String> dockerEnvironment) {
    this.dockerEnvironment = dockerEnvironment;
  }
}
