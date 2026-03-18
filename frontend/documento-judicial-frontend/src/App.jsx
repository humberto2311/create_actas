import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './styles/theme';
import DocumentProcessForm from './components/DocumentProcessForm';

function App() {
  return (
    
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <DocumentProcessForm />
    </ThemeProvider>
  );
}

export default App;