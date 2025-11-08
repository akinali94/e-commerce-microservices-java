import React from 'react';
import './Footer.css';

const Footer = () => {
  const [deploymentDetails, setDeploymentDetails] = React.useState({});
  const currentYear = new Date().getFullYear();
  
  // In a real app, we might fetch deployment details from an API
  React.useEffect(() => {
    // Mock deployment details for demo
    setDeploymentDetails({
      CLUSTERNAME: 'demo-cluster',
      ZONE: 'us-central1-a',
      HOSTNAME: 'frontend-pod-123456'
    });
  }, []);

  return (
    <footer className="py-5">
      <div className="footer-top">
        <div className="container footer-social">
          <p className="footer-text">
            This website is hosted for demo purposes only. It is not an actual shop. This is not a Google product.
          </p>
          <p className="footer-text">
            © 2020-{currentYear} Google LLC (<a href="https://github.com/GoogleCloudPlatform/microservices-demo">Source Code</a>)
          </p>
          <p className="footer-text">
            <small>
              {/* Session ID would typically come from the context */}
              session-id: {localStorage.getItem('sessionId') || 'Not available'} — 
              request-id: {Math.random().toString(36).substring(2, 10)}
            </small>
            <br/>
            <small>
              {deploymentDetails ? (
                <>
                  {deploymentDetails.CLUSTERNAME && (
                    <><b>Cluster: </b>{deploymentDetails.CLUSTERNAME}<br/></>
                  )}
                  {deploymentDetails.ZONE && (
                    <><b>Zone: </b>{deploymentDetails.ZONE}<br/></>
                  )}
                  {deploymentDetails.HOSTNAME && (
                    <><b>Pod: </b>{deploymentDetails.HOSTNAME}</>
                  )}
                </>
              ) : (
                'Deployment details are still loading. Try refreshing this page.'
              )}
            </small>
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;