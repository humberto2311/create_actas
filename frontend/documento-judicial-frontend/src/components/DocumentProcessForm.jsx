import React, { useState, useEffect, useCallback, useRef } from 'react';
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Grid,
  FormControlLabel,
  Switch,
  MenuItem,
  Box,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  InputAdornment,
  Tab,
  Tabs,
  Chip
} from '@mui/material';
import {
  Save as SaveIcon,
  Refresh as RefreshIcon,
  Search as SearchIcon,
  Clear as ClearIcon,
  Add as AddIcon
} from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { toast, ToastContainer } from 'react-toastify';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';
import 'react-toastify/dist/ReactToastify.css';

import { documentService } from '../services/api';
import { documentProcessSchema, audienceTypes, nacionOptions } from '../utils/validation';
import DocumentCard from './DocumentCard';

const DocumentProcessForm = () => {
  const [loading, setLoading] = useState(false);
  const [documents, setDocuments] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedId, setSelectedId] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedDocument, setSelectedDocument] = useState(null);
  const [tabValue, setTabValue] = useState(0);
  const [deleteInProgress, setDeleteInProgress] = useState(false);
  
  const formRef = useRef(null);
  const isMounted = useRef(true);

  useEffect(() => {
    isMounted.current = true;
    return () => {
      isMounted.current = false;
    };
  }, []);

  const {
    control,
    handleSubmit,
    reset,
    setValue,
    formState: { errors }
  } = useForm({
    resolver: yupResolver(documentProcessSchema),
    defaultValues: {
      names: '',
      lastNames: '',
      identity: '',
      nacion: 'COLOMBIANA',
      date: new Date().toISOString().split('T')[0],
      captura: new Date().toISOString().split('T')[0],
      conduct: '',
      radicado: '',
      fiscal: '',
      typeAudience: '',
      fact: '',
      juzgado: '',
      state: true
    }
  });

  const loadDocuments = useCallback(async () => {
    if (!isMounted.current) return;
    
    setLoading(true);
    try {
      const data = await documentService.getAll();
      if (isMounted.current) {
        setDocuments(data);
      }
    } catch (error) {
      if (isMounted.current) {
        toast.error('Error al cargar los documentos');
        console.error('Error:', error);
      }
    } finally {
      if (isMounted.current) {
        setLoading(false);
      }
    }
  }, []);

  useEffect(() => {
    loadDocuments();
  }, [loadDocuments]);

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      console.log('Enviando datos:', data);
      
      // Convertir fechas a formato ISO
      const formattedData = {
        ...data,
        date: data.date instanceof Date ? data.date.toISOString().split('T')[0] : data.date,
        captura: data.captura instanceof Date ? data.captura.toISOString().split('T')[0] : data.captura
      };
      
      if (selectedId) {
        await documentService.update(selectedId, formattedData);
        toast.success('Documento actualizado exitosamente');
      } else {
        await documentService.create(formattedData);
        toast.success('Documento guardado exitosamente');
      }
      
      // Resetear el formulario ANTES de recargar
      reset();
      setSelectedId(null);
      
      // Recargar documentos
      await loadDocuments();
      
      // Cambiar a la pestaña de "Todos"
      setTabValue(0);
      
    } catch (error) {
      console.error('Error detallado:', error);
      
      if (error.response?.status === 500) {
        if (error.response?.data?.message?.includes('duplicate key')) {
          toast.error('Ya existe un registro con esta identificación');
        } else {
          toast.error('Error interno del servidor. Por favor intente más tarde');
        }
      } else {
        toast.error('Error al conectar con el servidor');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (deleteInProgress) return;
    
    if (window.confirm('¿Está seguro de eliminar este documento?')) {
      setDeleteInProgress(true);
      
      try {
        await documentService.delete(id);
        toast.success('Documento eliminado exitosamente');
        
        // Actualizar el estado local
        setDocuments(prev => prev.filter(doc => doc.id !== id));
        
        if (selectedId === id) {
          reset();
          setSelectedId(null);
        }
      } catch (error) {
        console.error('Error al eliminar:', error);
        toast.error('Error al eliminar el documento');
        // Recargar en caso de error
        await loadDocuments();
      } finally {
        setDeleteInProgress(false);
      }
    }
  };

  const handleEdit = (doc) => {
    // Resetear el formulario con los nuevos valores
    reset(doc);
    setSelectedId(doc.id);
    setTabValue(0);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleView = (doc) => {
    setSelectedDocument(doc);
    setOpenDialog(true);
  };

  const handleGenerateDocument = async (id) => {
    try {
      const result = await documentService.generateZip(id);
      
      const url = window.URL.createObjectURL(result.blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', result.filename);
      document.body.appendChild(link);
      link.click();
      
      setTimeout(() => {
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      }, 100);
      
      toast.success('Documentos generados exitosamente');
    } catch (error) {
      toast.error('Error al generar los documentos');
      console.error('Error:', error);
    }
  };

  const handleNewDocument = () => {
    reset();
    setSelectedId(null);
    setTabValue(0);
  };

  const filteredDocuments = documents.filter(doc => 
    doc.names?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doc.lastNames?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doc.identity?.includes(searchTerm)
  );

  const activeDocuments = filteredDocuments.filter(doc => doc.state);
  const inactiveDocuments = filteredDocuments.filter(doc => !doc.state);

  const getDocumentsToShow = () => {
    switch (tabValue) {
      case 1:
        return activeDocuments;
      case 2:
        return inactiveDocuments;
      default:
        return filteredDocuments;
    }
  };

  const documentsToShow = getDocumentsToShow();

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <ToastContainer position="top-right" autoClose={3000} />
      
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" component="h1" sx={{ fontWeight: 'bold', color: 'primary.main' }}>
          Gestión de Procesos Judiciales
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={handleNewDocument}
        >
          Nuevo Documento
        </Button>
      </Box>

      <Grid container spacing={3}>
        {/* Formulario */}
        <Grid size={{ xs: 12, md: 5 }}>
          <Paper elevation={3} sx={{ p: 3, borderRadius: 2, position: 'sticky', top: 20 }}>
            <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', mb: 3 }}>
              {selectedId ? '✏️ Editar Documento' : '📄 Nuevo Documento'}
            </Typography>

            <form ref={formRef} onSubmit={handleSubmit(onSubmit)}>
              <Grid container spacing={2}>
                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="names"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Nombres *"
                        fullWidth
                        error={!!errors.names}
                        helperText={errors.names?.message}
                        variant="outlined"
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="lastNames"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Apellidos *"
                        fullWidth
                        error={!!errors.lastNames}
                        helperText={errors.lastNames?.message}
                        variant="outlined"
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="identity"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Identificación *"
                        fullWidth
                        error={!!errors.identity}
                        helperText={errors.identity?.message}
                        variant="outlined"
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="nacion"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        select
                        label="Nacionalidad *"
                        fullWidth
                        error={!!errors.nacion}
                        helperText={errors.nacion?.message}
                        variant="outlined"
                        size="small"
                      >
                        <MenuItem value="">Seleccionar...</MenuItem>
                        {nacionOptions.map((option) => (
                          <MenuItem key={option.value} value={option.value}>
                            {option.label}
                          </MenuItem>
                        ))}
                      </TextField>
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="date"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Fecha del Proceso *"
                        type="date"
                        fullWidth
                        error={!!errors.date}
                        helperText={errors.date?.message}
                        variant="outlined"
                        size="small"
                        InputLabelProps={{ shrink: true }}
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="captura"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Fecha de Captura *"
                        type="date"
                        fullWidth
                        error={!!errors.captura}
                        helperText={errors.captura?.message}
                        variant="outlined"
                        size="small"
                        InputLabelProps={{ shrink: true }}
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="radicado"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Radicado"
                        fullWidth
                        error={!!errors.radicado}
                        helperText={errors.radicado?.message}
                        variant="outlined"
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="fiscal"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Fiscal"
                        fullWidth
                        error={!!errors.fiscal}
                        helperText={errors.fiscal?.message}
                        variant="outlined"
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12, sm: 6 }}>
                  <Controller
                    name="juzgado"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Juzgado"
                        fullWidth
                        error={!!errors.juzgado}
                        helperText={errors.juzgado?.message}
                        variant="outlined"
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Controller
                    name="conduct"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Delito/Conducta"
                        fullWidth
                        error={!!errors.conduct}
                        helperText={errors.conduct?.message}
                        variant="outlined"
                        size="small"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Controller
                    name="fact"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        label="Descripción de los Hechos"
                        fullWidth
                        multiline
                        rows={3}
                        error={!!errors.fact}
                        helperText={errors.fact?.message}
                        variant="outlined"
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Typography variant="subtitle2" color="primary" gutterBottom sx={{ mt: 1 }}>
                    Estado del Proceso
                  </Typography>
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Controller
                    name="state"
                    control={control}
                    render={({ field: { value, onChange } }) => (
                      <FormControlLabel
                        control={
                          <Switch
                            checked={value}
                            onChange={onChange}
                            color="primary"
                          />
                        }
                        label={value ? "Activo" : "Inactivo"}
                      />
                    )}
                  />
                </Grid>

                <Grid size={{ xs: 12 }}>
                  <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end', mt: 2 }}>
                    {selectedId && (
                      <Button
                        variant="outlined"
                        onClick={handleNewDocument}
                      >
                        Cancelar
                      </Button>
                    )}
                    <Button
                      type="submit"
                      variant="contained"
                      color="primary"
                      startIcon={<SaveIcon />}
                      disabled={loading}
                    >
                      {loading ? <CircularProgress size={24} /> : (selectedId ? 'Actualizar' : 'Guardar')}
                    </Button>
                  </Box>
                </Grid>
              </Grid>
            </form>
          </Paper>
        </Grid>

        {/* Lista de Documentos */}
        <Grid size={{ xs: 12, md: 7 }}>
          <Paper elevation={3} sx={{ p: 3, borderRadius: 2 }}>
            <Box sx={{ mb: 3 }}>
              <TextField
                fullWidth
                size="small"
                placeholder="Buscar por nombre o identificación..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <SearchIcon color="action" />
                    </InputAdornment>
                  ),
                  endAdornment: searchTerm && (
                    <InputAdornment position="end">
                      <IconButton size="small" onClick={() => setSearchTerm('')}>
                        <ClearIcon />
                      </IconButton>
                    </InputAdornment>
                  )
                }}
              />
            </Box>

            <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} sx={{ mb: 2 }}>
              <Tab label={`Todos (${filteredDocuments.length})`} />
              <Tab label={`Activos (${activeDocuments.length})`} />
              <Tab label={`Inactivos (${inactiveDocuments.length})`} />
            </Tabs>

            <Box sx={{ maxHeight: 600, overflow: 'auto', pr: 1 }}>
              {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                  <CircularProgress />
                </Box>
              ) : (
                <>
                  {documentsToShow.map((doc) => (
                    <DocumentCard
                      key={`doc-${doc.id}`}
                      document={doc}
                      onEdit={handleEdit}
                      onDelete={handleDelete}
                      onGenerate={handleGenerateDocument}
                      onView={handleView}
                    />
                  ))}
                  
                  {documentsToShow.length === 0 && (
                    <Alert severity="info" sx={{ mt: 2 }}>
                      No hay documentos para mostrar
                    </Alert>
                  )}
                </>
              )}
            </Box>

            <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
              <Button
                startIcon={<RefreshIcon />}
                onClick={loadDocuments}
                size="small"
              >
                Actualizar
              </Button>
            </Box>
          </Paper>
        </Grid>
      </Grid>

      {/* Diálogo de detalles */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>Detalles del Documento</DialogTitle>
        <DialogContent dividers>
          {selectedDocument && (
            <Grid container spacing={2}>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2" color="primary">Nombres</Typography>
                <Typography variant="body1" gutterBottom>{selectedDocument.names} {selectedDocument.lastNames}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2" color="primary">Identificación</Typography>
                <Typography variant="body1" gutterBottom>{selectedDocument.identity}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2" color="primary">Nacionalidad</Typography>
                <Typography variant="body1" gutterBottom>
                  {nacionOptions.find(n => n.value === selectedDocument.nacion)?.label || selectedDocument.nacion}
                </Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2" color="primary">Fecha del Proceso</Typography>
                <Typography variant="body1" gutterBottom>
                  {selectedDocument.date && format(new Date(selectedDocument.date), 'PPP', { locale: es })}
                </Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2" color="primary">Fecha de Captura</Typography>
                <Typography variant="body1" gutterBottom>
                  {selectedDocument.captura && format(new Date(selectedDocument.captura), 'PPP', { locale: es })}
                </Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="subtitle2" color="primary">Estado del Proceso</Typography>
                <Chip 
                  label={selectedDocument.state ? 'Activo' : 'Inactivo'}
                  color={selectedDocument.state ? 'success' : 'error'}
                  size="small"
                />
              </Grid>
              {selectedDocument.radicado && (
                <Grid size={{ xs: 6 }}>
                  <Typography variant="subtitle2" color="primary">Radicado</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.radicado}</Typography>
                </Grid>
              )}
              {selectedDocument.fiscal && (
                <Grid size={{ xs: 6 }}>
                  <Typography variant="subtitle2" color="primary">Fiscal</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.fiscal}</Typography>
                </Grid>
              )}
              {selectedDocument.typeAudience && (
                <Grid size={{ xs: 6 }}>
                  <Typography variant="subtitle2" color="primary">Tipo de Audiencia</Typography>
                  <Typography variant="body1" gutterBottom>
                    {audienceTypes.find(t => t.value === selectedDocument.typeAudience)?.label || selectedDocument.typeAudience}
                  </Typography>
                </Grid>
              )}
              {selectedDocument.juzgado && (
                <Grid size={{ xs: 6 }}>
                  <Typography variant="subtitle2" color="primary">Juzgado</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.juzgado}</Typography>
                </Grid>
              )}
              {selectedDocument.conduct && (
                <Grid size={{ xs: 12 }}>
                  <Typography variant="subtitle2" color="primary">Conducta</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.conduct}</Typography>
                </Grid>
              )}
              {selectedDocument.fact && (
                <Grid size={{ xs: 12 }}>
                  <Typography variant="subtitle2" color="primary">Hechos</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.fact}</Typography>
                </Grid>
              )}
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)} color="primary">
            Cerrar
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default DocumentProcessForm;