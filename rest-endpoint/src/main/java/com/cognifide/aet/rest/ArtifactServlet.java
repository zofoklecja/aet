/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.rest;

import com.cognifide.aet.vs.Artifact;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

@Service
@Component(label = "ArtifactDataContent", description = "Data Storage information service", immediate = true)
public class ArtifactServlet extends BasicDataServlet {

  private static final long serialVersionUID = 1867870883439947956L;

  @Reference
  private ArtifactsDAO artifactsDAO;

  @Override
  protected void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String id = req.getParameter(Helper.ID_PARAM);
    resp.setCharacterEncoding("UTF-8");
    Artifact artifact = artifactsDAO.getArtifact(dbKey, id);
    if (artifact != null) {
      resp.setContentType(artifact.getContentType());
      resp.setHeader("Cache-Control", "public, max-age=31536000");

      OutputStream output = resp.getOutputStream();
      IOUtils.copy(artifact.getArtifactStream(), output);
      output.flush();
    } else {
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.setContentType("application/json");
      resp.getWriter().write(
          responseAsJson("Unable to get artifact with id : %s form %s", id, dbKey.toString()));
    }
  }

  @Activate
  public void start() {
    register(Helper.getArtifactPath());

  }

  @Deactivate
  public void stop() {
    unregister(Helper.getArtifactPath());
  }
}
