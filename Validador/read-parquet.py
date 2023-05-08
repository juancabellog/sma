import polars as pl

data = pl.read_parquet('/Users/manolocabello/Downloads/parquet-sma/CalidadDelAire_UfId_DispositivoId_SMA_224_479.parquet')        
print('leyo ok', data)
