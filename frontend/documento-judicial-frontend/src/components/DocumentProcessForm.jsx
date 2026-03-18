import React, { useState, useEffect } from 'react';
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

  useEffect(() => {
    loadDocuments();
  }, []);

  const loadDocuments = async () => {
    setLoading(true);
    try {
      const data = await documentService.getAll();
      setDocuments(data);
    } catch (error) {
      toast.error('Error al cargar los documentos');
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

 const onSubmit = async (data) => {
  setLoading(true);
  try {
    console.log('Enviando datos:', data);
    
    let response;
    if (selectedId) {
      response = await documentService.update(selectedId, data);
      toast.success('Documento actualizado exitosamente');
    } else {
      response = await documentService.create(data);
      toast.success('Documento guardado exitosamente');
    }
    
    // Solo resetear si la operación fue exitosa
    reset();
    setSelectedId(null);
    
    // Recargar la lista de documentos
    await loadDocuments();
    
    // Cambiar a la pestaña de "Todos"
    setTabValue(0);
    
  } catch (error) {
    console.error('Error detallado:', error);
    
    // Mensajes de error más específicos
    if (error.response?.status === 500) {
      if (error.response?.data?.message?.includes('duplicate key')) {
        toast.error('Ya existe un registro con esta identificación');
      } else if (error.response?.data?.message?.includes('nacion')) {
        toast.error('Error con la nacionalidad. Por favor contacte al administrador');
      } else {
        toast.error('Error interno del servidor. Por favor intente más tarde');
      }
    } else if (error.response?.status === 409) {
      toast.error('Conflicto: Ya existe un registro con estos datos');
    } else {
      toast.error('Error al conectar con el servidor');
    }
  } finally {
    setLoading(false);
  }
};

 const handleDelete = async (id, event) => {
  // Prevenir cualquier comportamiento por defecto
  if (event) {
    event.preventDefault();
    event.stopPropagation();
  }
  
  if (window.confirm('¿Está seguro de eliminar este documento?')) {
    try {
      setLoading(true); // Activar loading
      await documentService.delete(id);
      toast.success('Documento eliminado exitosamente');
      await loadDocuments(); // Esperar a que se carguen los documentos
      
      if (selectedId === id) {
        reset();
        setSelectedId(null);
      }
    } catch (error) {
      console.error('Error al eliminar:', error);
      toast.error('Error al eliminar el documento');
    } finally {
      setLoading(false); // Desactivar loading
    }
  }
};
  const handleEdit = (doc) => {
    setSelectedId(doc.id);
    Object.keys(doc).forEach(key => {
      setValue(key, doc[key]);
    });
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
      link.remove();
      
      window.URL.revokeObjectURL(url);
      toast.success('Documentos generados exitosamente');
    } catch (error) {
      toast.error('Error al generar los documentos');
      console.error('Error:', error);
    }
  };

  const handleNewDocument = () => {
    reset({
      names: '',
      lastNames: '',
      identity: '',
      nacion: '',
      date: new Date().toISOString().split('T')[0],
      captura: new Date().toISOString().split('T')[0],
      conduct: '',
      radicado: '',
      fiscal: '',
      typeAudience: '',
      fact: '',
      juzgado: '',
      state: true
    });
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
        <Grid item xs={12} md={5}>
          <Paper elevation={3} sx={{ p: 3, borderRadius: 2, position: 'sticky', top: 20 }}>
            <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', mb: 3 }}>
              {selectedId ? '✏️ Editar Documento' : '📄 Nuevo Documento'}
            </Typography>

            <form onSubmit={handleSubmit(onSubmit)}>
              <Grid container spacing={2}>
                {/* Datos Personales */}
              

                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={6}>
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

              
                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={6}>
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

              

                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12} sm={6}>
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

               

                <Grid item xs={12} sm={6}>
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

                <Grid item xs={12}>
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

                <Grid item xs={12}>
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

                {/* Estado */}
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="primary" gutterBottom sx={{ mt: 1 }}>
                    Estado del Proceso
                  </Typography>
                </Grid>

                <Grid item xs={12}>
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

                {/* Botones */}
                <Grid item xs={12}>
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
        <Grid item xs={12} md={7}>
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
                  {tabValue === 0 && filteredDocuments.map(doc => (
                   <DocumentCard
  key={doc.id}
  document={doc}
  onEdit={handleEdit}
  onDelete={(id, event) => handleDelete(id, event)}
  onGenerate={(id, event) => {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    handleGenerateDocument(id);
  }}
  onView={(doc, event) => handleView(doc, event)}
/>
                  ))}
                  {tabValue === 1 && activeDocuments.map(doc => (
                    <DocumentCard
                      key={doc.id}
                      document={doc}
                      onEdit={handleEdit}
                      onDelete={handleDelete}
                      onGenerate={handleGenerateDocument}
                      onView={handleView}
                    />
                  ))}
                  {tabValue === 2 && inactiveDocuments.map(doc => (
                    <DocumentCard
                      key={doc.id}
                      document={doc}
                      onEdit={handleEdit}
                      onDelete={handleDelete}
                      onGenerate={handleGenerateDocument}
                      onView={handleView}
                    />
                  ))}
                  
                  {filteredDocuments.length === 0 && (
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
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="primary">Nombres</Typography>
                <Typography variant="body1" gutterBottom>{selectedDocument.names} {selectedDocument.lastNames}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="primary">Identificación</Typography>
                <Typography variant="body1" gutterBottom>{selectedDocument.identity}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="primary">Nacionalidad</Typography>
                <Typography variant="body1" gutterBottom>
                  {nacionOptions.find(n => n.value === selectedDocument.nacion)?.label || selectedDocument.nacion}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="primary">Fecha del Proceso</Typography>
                <Typography variant="body1" gutterBottom>
                  {selectedDocument.date && format(new Date(selectedDocument.date), 'PPP', { locale: es })}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="primary">Fecha de Captura</Typography>
                <Typography variant="body1" gutterBottom>
                  {selectedDocument.captura && format(new Date(selectedDocument.captura), 'PPP', { locale: es })}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="primary"> Estado del Proceso</Typography>
                <Chip 
                  label={selectedDocument.state ? 'Activo' : 'Inactivo'}
                  color={selectedDocument.state ? 'success' : 'error'}
                  size="small"
                />
              </Grid>
              {selectedDocument.radicado && (
                <Grid item xs={6}>
                  <Typography variant="subtitle2" color="primary">Radicado</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.radicado}</Typography>
                </Grid>
              )}
              {selectedDocument.fiscal && (
                <Grid item xs={6}>
                  <Typography variant="subtitle2" color="primary">Fiscal</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.fiscal}</Typography>
                </Grid>
              )}
              {selectedDocument.typeAudience && (
                <Grid item xs={6}>
                  <Typography variant="subtitle2" color="primary">Tipo de Audiencia</Typography>
                  <Typography variant="body1" gutterBottom>
                    {audienceTypes.find(t => t.value === selectedDocument.typeAudience)?.label || selectedDocument.typeAudience}
                  </Typography>
                </Grid>
              )}
              {selectedDocument.juzgado && (
                <Grid item xs={6}>
                  <Typography variant="subtitle2" color="primary">Juzgado</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.juzgado}</Typography>
                </Grid>
              )}
              {selectedDocument.conduct && (
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="primary">Conducta</Typography>
                  <Typography variant="body1" gutterBottom>{selectedDocument.conduct}</Typography>
                </Grid>
              )}
              {selectedDocument.fact && (
                <Grid item xs={12}>
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