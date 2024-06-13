package net.minecraft;

import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceImmutableList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SingleKeyCache;
import net.minecraft.util.TimeSource;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public class Util {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_MAX_THREADS = 255;
    private static final int f_303362_ = 10;
    private static final String MAX_THREADS_SYSTEM_PROPERTY = "max.bg.threads";
    private static final ExecutorService BACKGROUND_EXECUTOR = makeExecutor("Main");
    private static final ExecutorService IO_POOL = makeIoExecutor("IO-Worker-", false);
    private static final ExecutorService f_302521_ = makeIoExecutor("Download-", true);
    private static final DateTimeFormatter FILENAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss", Locale.ROOT);
    public static final int f_303450_ = 8;
    public static final long NANOS_PER_MILLI = 1000000L;
    public static TimeSource.NanoTimeSource timeSource = System::nanoTime;
    public static final Ticker TICKER = new Ticker() {
        @Override
        public long read() {
            return Util.timeSource.getAsLong();
        }
    };
    public static final UUID NIL_UUID = new UUID(0L, 0L);
    public static final FileSystemProvider ZIP_FILE_SYSTEM_PROVIDER = FileSystemProvider.installedProviders()
        .stream()
        .filter(p_201865_ -> p_201865_.getScheme().equalsIgnoreCase("jar"))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No jar file system provider found"));
    private static Consumer<String> thePauser = p_201905_ -> {
    };

    public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Entry::getKey, Entry::getValue);
    }

    public static <T> Collector<T, ?, List<T>> m_323807_() {
        return Collectors.toCollection(Lists::newArrayList);
    }

    public static <T extends Comparable<T>> String getPropertyName(Property<T> pProperty, Object pValue) {
        return pProperty.getName((T)pValue);
    }

    public static String makeDescriptionId(String pType, @Nullable ResourceLocation pId) {
        return pId == null
            ? pType + ".unregistered_sadface"
            : pType + "." + pId.getNamespace() + "." + pId.getPath().replace('/', '.');
    }

    public static long getMillis() {
        return getNanos() / 1000000L;
    }

    public static long getNanos() {
        return timeSource.getAsLong();
    }

    public static long getEpochMillis() {
        return Instant.now().toEpochMilli();
    }

    public static String getFilenameFormattedDateTime() {
        return FILENAME_DATE_TIME_FORMATTER.format(ZonedDateTime.now());
    }

    private static ExecutorService makeExecutor(String pServiceName) {
        int i = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxThreads());
        ExecutorService executorservice;
        if (i <= 0) {
            executorservice = MoreExecutors.newDirectExecutorService();
        } else {
            AtomicInteger atomicinteger = new AtomicInteger(1);
            executorservice = new ForkJoinPool(i, p_308084_ -> {
                ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(p_308084_) {
                    @Override
                    protected void onTermination(Throwable p_211561_) {
                        if (p_211561_ != null) {
                            Util.LOGGER.warn("{} died", this.getName(), p_211561_);
                        } else {
                            Util.LOGGER.debug("{} shutdown", this.getName());
                        }

                        super.onTermination(p_211561_);
                    }
                };
                forkjoinworkerthread.setName("Worker-" + pServiceName + "-" + atomicinteger.getAndIncrement());
                return forkjoinworkerthread;
            }, Util::onThreadException, true);
        }

        return executorservice;
    }

    private static int getMaxThreads() {
        String s = System.getProperty("max.bg.threads");
        if (s != null) {
            try {
                int i = Integer.parseInt(s);
                if (i >= 1 && i <= 255) {
                    return i;
                }

                LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", "max.bg.threads", s, 255);
            } catch (NumberFormatException numberformatexception) {
                LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", "max.bg.threads", s, 255);
            }
        }

        return 255;
    }

    public static ExecutorService backgroundExecutor() {
        return BACKGROUND_EXECUTOR;
    }

    public static ExecutorService ioPool() {
        return IO_POOL;
    }

    public static ExecutorService m_306705_() {
        return f_302521_;
    }

    public static void shutdownExecutors() {
        shutdownExecutor(BACKGROUND_EXECUTOR);
        shutdownExecutor(IO_POOL);
    }

    private static void shutdownExecutor(ExecutorService pService) {
        pService.shutdown();

        boolean flag;
        try {
            flag = pService.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedexception) {
            flag = false;
        }

        if (!flag) {
            pService.shutdownNow();
        }
    }

    private static ExecutorService makeIoExecutor(String p_309722_, boolean p_310621_) {
        AtomicInteger atomicinteger = new AtomicInteger(1);
        return Executors.newCachedThreadPool(p_308081_ -> {
            Thread thread = new Thread(p_308081_);
            thread.setName(p_309722_ + atomicinteger.getAndIncrement());
            thread.setDaemon(p_310621_);
            thread.setUncaughtExceptionHandler(Util::onThreadException);
            return thread;
        });
    }

    public static void throwAsRuntime(Throwable pThrowable) {
        throw pThrowable instanceof RuntimeException ? (RuntimeException)pThrowable : new RuntimeException(pThrowable);
    }

    private static void onThreadException(Thread p_137496_, Throwable p_137497_) {
        pauseInIde(p_137497_);
        if (p_137497_ instanceof CompletionException) {
            p_137497_ = p_137497_.getCause();
        }

        if (p_137497_ instanceof ReportedException reportedexception) {
            Bootstrap.realStdoutPrintln(reportedexception.getReport().getFriendlyReport());
            System.exit(-1);
        }

        LOGGER.error(String.format(Locale.ROOT, "Caught exception in thread %s", p_137496_), p_137497_);
    }

    @Nullable
    public static Type<?> fetchChoiceType(TypeReference pType, String pChoiceName) {
        return !SharedConstants.CHECK_DATA_FIXER_SCHEMA ? null : doFetchChoiceType(pType, pChoiceName);
    }

    @Nullable
    private static Type<?> doFetchChoiceType(TypeReference pType, String pChoiceName) {
        Type<?> type = null;

        try {
            type = DataFixers.getDataFixer()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion()))
                .getChoiceType(pType, pChoiceName);
        } catch (IllegalArgumentException illegalargumentexception) {
            LOGGER.debug("No data fixer registered for {}", pChoiceName);
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw illegalargumentexception;
            }
        }

        return type;
    }

    public static Runnable wrapThreadWithTaskName(String pName, Runnable pTask) {
        return SharedConstants.IS_RUNNING_IN_IDE ? () -> {
            Thread thread = Thread.currentThread();
            String s = thread.getName();
            thread.setName(pName);

            try {
                pTask.run();
            } finally {
                thread.setName(s);
            }
        } : pTask;
    }

    public static <V> Supplier<V> wrapThreadWithTaskName(String pName, Supplier<V> pTask) {
        return SharedConstants.IS_RUNNING_IN_IDE ? () -> {
            Thread thread = Thread.currentThread();
            String s = thread.getName();
            thread.setName(pName);

            Object object;
            try {
                object = pTask.get();
            } finally {
                thread.setName(s);
            }

            return (V)object;
        } : pTask;
    }

    public static <T> String m_322642_(Registry<T> p_336230_, T p_335370_) {
        ResourceLocation resourcelocation = p_336230_.getKey(p_335370_);
        return resourcelocation == null ? "[unregistered]" : resourcelocation.toString();
    }

    public static <T> Predicate<T> m_322468_(List<? extends Predicate<T>> p_333513_) {
        List<Predicate<T>> list = List.copyOf(p_333513_);

        return switch (list.size()) {
            case 0 -> p_325171_ -> true;
            case 1 -> (Predicate)list.get(0);
            case 2 -> list.get(0).and(list.get(1));
            default -> p_325170_ -> {
            for (Predicate<T> predicate : list) {
                if (!predicate.test((T)p_325170_)) {
                    return false;
                }
            }

            return true;
        };
        };
    }

    public static <T> Predicate<T> m_321702_(List<? extends Predicate<T>> p_328136_) {
        List<Predicate<T>> list = List.copyOf(p_328136_);

        return switch (list.size()) {
            case 0 -> p_325174_ -> false;
            case 1 -> (Predicate)list.get(0);
            case 2 -> list.get(0).or(list.get(1));
            default -> p_325173_ -> {
            for (Predicate<T> predicate : list) {
                if (predicate.test((T)p_325173_)) {
                    return true;
                }
            }

            return false;
        };
        };
    }

    public static Util.OS getPlatform() {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (s.contains("win")) {
            return Util.OS.WINDOWS;
        } else if (s.contains("mac")) {
            return Util.OS.OSX;
        } else if (s.contains("solaris")) {
            return Util.OS.SOLARIS;
        } else if (s.contains("sunos")) {
            return Util.OS.SOLARIS;
        } else if (s.contains("linux")) {
            return Util.OS.LINUX;
        } else {
            return s.contains("unix") ? Util.OS.LINUX : Util.OS.UNKNOWN;
        }
    }

    public static Stream<String> getVmArguments() {
        RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
        return runtimemxbean.getInputArguments().stream().filter(p_201903_ -> p_201903_.startsWith("-X"));
    }

    public static <T> T lastOf(List<T> pList) {
        return pList.get(pList.size() - 1);
    }

    public static <T> T findNextInIterable(Iterable<T> pIterable, @Nullable T pElement) {
        Iterator<T> iterator = pIterable.iterator();
        T t = iterator.next();
        if (pElement != null) {
            T t1 = t;

            while (t1 != pElement) {
                if (iterator.hasNext()) {
                    t1 = iterator.next();
                }
            }

            if (iterator.hasNext()) {
                return iterator.next();
            }
        }

        return t;
    }

    public static <T> T findPreviousInIterable(Iterable<T> pIterable, @Nullable T pCurrent) {
        Iterator<T> iterator = pIterable.iterator();
        T t = null;

        while (iterator.hasNext()) {
            T t1 = iterator.next();
            if (t1 == pCurrent) {
                if (t == null) {
                    t = iterator.hasNext() ? Iterators.getLast(iterator) : pCurrent;
                }
                break;
            }

            t = t1;
        }

        return t;
    }

    public static <T> T make(Supplier<T> pSupplier) {
        return pSupplier.get();
    }

    public static <T> T make(T pObject, Consumer<? super T> pConsumer) {
        pConsumer.accept(pObject);
        return pObject;
    }

    public static <V> CompletableFuture<List<V>> sequence(List<? extends CompletableFuture<V>> pFutures) {
        if (pFutures.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        } else if (pFutures.size() == 1) {
            return pFutures.get(0).thenApply(List::of);
        } else {
            CompletableFuture<Void> completablefuture = CompletableFuture.allOf(pFutures.toArray(new CompletableFuture[0]));
            return completablefuture.thenApply(p_203746_ -> pFutures.stream().map(CompletableFuture::join).toList());
        }
    }

    public static <V> CompletableFuture<List<V>> sequenceFailFast(List<? extends CompletableFuture<? extends V>> pCompletableFutures) {
        CompletableFuture<List<V>> completablefuture = new CompletableFuture<>();
        return fallibleSequence(pCompletableFutures, completablefuture::completeExceptionally).applyToEither(completablefuture, Function.identity());
    }

    public static <V> CompletableFuture<List<V>> sequenceFailFastAndCancel(List<? extends CompletableFuture<? extends V>> pCompletableFutures) {
        CompletableFuture<List<V>> completablefuture = new CompletableFuture<>();
        return fallibleSequence(pCompletableFutures, p_274642_ -> {
            if (completablefuture.completeExceptionally(p_274642_)) {
                for (CompletableFuture<? extends V> completablefuture1 : pCompletableFutures) {
                    completablefuture1.cancel(true);
                }
            }
        }).applyToEither(completablefuture, Function.identity());
    }

    private static <V> CompletableFuture<List<V>> fallibleSequence(List<? extends CompletableFuture<? extends V>> pCompletableFutures, Consumer<Throwable> pThrowableConsumer) {
        List<V> list = Lists.newArrayListWithCapacity(pCompletableFutures.size());
        CompletableFuture<?>[] completablefuture = new CompletableFuture[pCompletableFutures.size()];
        pCompletableFutures.forEach(p_214641_ -> {
            int i = list.size();
            list.add(null);
            completablefuture[i] = p_214641_.whenComplete((p_214650_, p_214651_) -> {
                if (p_214651_ != null) {
                    pThrowableConsumer.accept(p_214651_);
                } else {
                    list.set(i, (V)p_214650_);
                }
            });
        });
        return CompletableFuture.allOf(completablefuture).thenApply(p_214626_ -> list);
    }

    public static <T> Optional<T> ifElse(Optional<T> pOpt, Consumer<T> pConsumer, Runnable pOrElse) {
        if (pOpt.isPresent()) {
            pConsumer.accept(pOpt.get());
        } else {
            pOrElse.run();
        }

        return pOpt;
    }

    public static <T> Supplier<T> name(Supplier<T> pItem, Supplier<String> pNameSupplier) {
        return pItem;
    }

    public static Runnable name(Runnable pItem, Supplier<String> pNameSupplier) {
        return pItem;
    }

    public static void logAndPauseIfInIde(String pError) {
        LOGGER.error(pError);
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            doPause(pError);
        }
    }

    public static void logAndPauseIfInIde(String pMsg, Throwable pErr) {
        LOGGER.error(pMsg, pErr);
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            doPause(pMsg);
        }
    }

    public static <T extends Throwable> T pauseInIde(T pThrowable) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            LOGGER.error("Trying to throw a fatal exception, pausing in IDE", pThrowable);
            doPause(pThrowable.getMessage());
        }

        return pThrowable;
    }

    public static void setPause(Consumer<String> pThePauser) {
        thePauser = pThePauser;
    }

    private static void doPause(String pMsg) {
        Instant instant = Instant.now();
        LOGGER.warn("Did you remember to set a breakpoint here?");
        boolean flag = Duration.between(instant, Instant.now()).toMillis() > 500L;
        if (!flag) {
            thePauser.accept(pMsg);
        }
    }

    public static String describeError(Throwable pThrowable) {
        if (pThrowable.getCause() != null) {
            return describeError(pThrowable.getCause());
        } else {
            return pThrowable.getMessage() != null ? pThrowable.getMessage() : pThrowable.toString();
        }
    }

    public static <T> T getRandom(T[] pSelections, RandomSource pRandom) {
        return pSelections[pRandom.nextInt(pSelections.length)];
    }

    public static int getRandom(int[] pSelections, RandomSource pRandom) {
        return pSelections[pRandom.nextInt(pSelections.length)];
    }

    public static <T> T getRandom(List<T> pSelections, RandomSource pRandom) {
        return pSelections.get(pRandom.nextInt(pSelections.size()));
    }

    public static <T> Optional<T> getRandomSafe(List<T> pSelections, RandomSource pRandom) {
        return pSelections.isEmpty() ? Optional.empty() : Optional.of(getRandom(pSelections, pRandom));
    }

    private static BooleanSupplier createRenamer(final Path pFilePath, final Path pNewName) {
        return new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                try {
                    Files.move(pFilePath, pNewName);
                    return true;
                } catch (IOException ioexception) {
                    Util.LOGGER.error("Failed to rename", (Throwable)ioexception);
                    return false;
                }
            }

            @Override
            public String toString() {
                return "rename " + pFilePath + " to " + pNewName;
            }
        };
    }

    private static BooleanSupplier createDeleter(final Path pFilePath) {
        return new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                try {
                    Files.deleteIfExists(pFilePath);
                    return true;
                } catch (IOException ioexception) {
                    Util.LOGGER.warn("Failed to delete", (Throwable)ioexception);
                    return false;
                }
            }

            @Override
            public String toString() {
                return "delete old " + pFilePath;
            }
        };
    }

    private static BooleanSupplier createFileDeletedCheck(final Path pFilePath) {
        return new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return !Files.exists(pFilePath);
            }

            @Override
            public String toString() {
                return "verify that " + pFilePath + " is deleted";
            }
        };
    }

    private static BooleanSupplier createFileCreatedCheck(final Path pFilePath) {
        return new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return Files.isRegularFile(pFilePath);
            }

            @Override
            public String toString() {
                return "verify that " + pFilePath + " is present";
            }
        };
    }

    private static boolean executeInSequence(BooleanSupplier... pSuppliers) {
        for (BooleanSupplier booleansupplier : pSuppliers) {
            if (!booleansupplier.getAsBoolean()) {
                LOGGER.warn("Failed to execute {}", booleansupplier);
                return false;
            }
        }

        return true;
    }

    private static boolean runWithRetries(int pMaxTries, String pActionName, BooleanSupplier... pSuppliers) {
        for (int i = 0; i < pMaxTries; i++) {
            if (executeInSequence(pSuppliers)) {
                return true;
            }

            LOGGER.error("Failed to {}, retrying {}/{}", pActionName, i, pMaxTries);
        }

        LOGGER.error("Failed to {}, aborting, progress might be lost", pActionName);
        return false;
    }

    public static void safeReplaceFile(Path pCurrent, Path pLatest, Path pOldBackup) {
        safeReplaceOrMoveFile(pCurrent, pLatest, pOldBackup, false);
    }

    public static boolean safeReplaceOrMoveFile(Path p_311739_, Path p_310810_, Path p_310842_, boolean pMove) {
        if (Files.exists(p_311739_)
            && !runWithRetries(10, "create backup " + p_310842_, createDeleter(p_310842_), createRenamer(p_311739_, p_310842_), createFileCreatedCheck(p_310842_))) {
            return false;
        } else if (!runWithRetries(10, "remove old " + p_311739_, createDeleter(p_311739_), createFileDeletedCheck(p_311739_))) {
            return false;
        } else if (!runWithRetries(10, "replace " + p_311739_ + " with " + p_310810_, createRenamer(p_310810_, p_311739_), createFileCreatedCheck(p_311739_)) && !pMove) {
            runWithRetries(10, "restore " + p_311739_ + " from " + p_310842_, createRenamer(p_310842_, p_311739_), createFileCreatedCheck(p_311739_));
            return false;
        } else {
            return true;
        }
    }

    public static int offsetByCodepoints(String pText, int pCursorPos, int pDirection) {
        int i = pText.length();
        if (pDirection >= 0) {
            for (int j = 0; pCursorPos < i && j < pDirection; j++) {
                if (Character.isHighSurrogate(pText.charAt(pCursorPos++)) && pCursorPos < i && Character.isLowSurrogate(pText.charAt(pCursorPos))) {
                    pCursorPos++;
                }
            }
        } else {
            for (int k = pDirection; pCursorPos > 0 && k < 0; k++) {
                pCursorPos--;
                if (Character.isLowSurrogate(pText.charAt(pCursorPos)) && pCursorPos > 0 && Character.isHighSurrogate(pText.charAt(pCursorPos - 1))) {
                    pCursorPos--;
                }
            }
        }

        return pCursorPos;
    }

    public static Consumer<String> prefix(String pPrefix, Consumer<String> pExpectedSize) {
        return p_214645_ -> pExpectedSize.accept(pPrefix + p_214645_);
    }

    public static DataResult<int[]> fixedSize(IntStream pStream, int pSize) {
        int[] aint = pStream.limit((long)(pSize + 1)).toArray();
        if (aint.length != pSize) {
            Supplier<String> supplier = () -> "Input is not a list of " + pSize + " ints";
            return aint.length >= pSize ? DataResult.error(supplier, Arrays.copyOf(aint, pSize)) : DataResult.error(supplier);
        } else {
            return DataResult.success(aint);
        }
    }

    public static DataResult<long[]> fixedSize(LongStream pStream, int pExpectedSize) {
        long[] along = pStream.limit((long)(pExpectedSize + 1)).toArray();
        if (along.length != pExpectedSize) {
            Supplier<String> supplier = () -> "Input is not a list of " + pExpectedSize + " longs";
            return along.length >= pExpectedSize ? DataResult.error(supplier, Arrays.copyOf(along, pExpectedSize)) : DataResult.error(supplier);
        } else {
            return DataResult.success(along);
        }
    }

    public static <T> DataResult<List<T>> fixedSize(List<T> pList, int pExpectedSize) {
        if (pList.size() != pExpectedSize) {
            Supplier<String> supplier = () -> "Input is not a list of " + pExpectedSize + " elements";
            return pList.size() >= pExpectedSize ? DataResult.error(supplier, pList.subList(0, pExpectedSize)) : DataResult.error(supplier);
        } else {
            return DataResult.success(pList);
        }
    }

    public static void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread") {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException interruptedexception) {
                        Util.LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                        return;
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    public static void copyBetweenDirs(Path pFromDirectory, Path pToDirectory, Path pFilePath) throws IOException {
        Path path = pFromDirectory.relativize(pFilePath);
        Path path1 = pToDirectory.resolve(path);
        Files.copy(pFilePath, path1);
    }

    public static String sanitizeName(String pFileName, CharPredicate pCharacterValidator) {
        return pFileName.toLowerCase(Locale.ROOT)
            .chars()
            .mapToObj(p_214666_ -> pCharacterValidator.test((char)p_214666_) ? Character.toString((char)p_214666_) : "_")
            .collect(Collectors.joining());
    }

    public static <K, V> SingleKeyCache<K, V> singleKeyCache(Function<K, V> pComputeValue) {
        return new SingleKeyCache<>(pComputeValue);
    }

    public static <T, R> Function<T, R> memoize(final Function<T, R> pMemoFunction) {
        return new Function<T, R>() {
            private final Map<T, R> cache = new ConcurrentHashMap<>();

            @Override
            public R apply(T p_214691_) {
                return this.cache.computeIfAbsent(p_214691_, pMemoFunction);
            }

            @Override
            public String toString() {
                return "memoize/1[function=" + pMemoFunction + ", size=" + this.cache.size() + "]";
            }
        };
    }

    public static <T, U, R> BiFunction<T, U, R> memoize(final BiFunction<T, U, R> pMemoBiFunction) {
        return new BiFunction<T, U, R>() {
            private final Map<Pair<T, U>, R> cache = new ConcurrentHashMap<>();

            @Override
            public R apply(T p_214700_, U p_214701_) {
                return this.cache.computeIfAbsent(Pair.of(p_214700_, p_214701_), p_214698_ -> pMemoBiFunction.apply(p_214698_.getFirst(), p_214698_.getSecond()));
            }

            @Override
            public String toString() {
                return "memoize/2[function=" + pMemoBiFunction + ", size=" + this.cache.size() + "]";
            }
        };
    }

    public static <T> List<T> toShuffledList(Stream<T> pStream, RandomSource pRandom) {
        ObjectArrayList<T> objectarraylist = pStream.collect(ObjectArrayList.toList());
        shuffle(objectarraylist, pRandom);
        return objectarraylist;
    }

    public static IntArrayList toShuffledList(IntStream pStream, RandomSource pRandom) {
        IntArrayList intarraylist = IntArrayList.wrap(pStream.toArray());
        int i = intarraylist.size();

        for (int j = i; j > 1; j--) {
            int k = pRandom.nextInt(j);
            intarraylist.set(j - 1, intarraylist.set(k, intarraylist.getInt(j - 1)));
        }

        return intarraylist;
    }

    public static <T> List<T> shuffledCopy(T[] pArray, RandomSource pRandom) {
        ObjectArrayList<T> objectarraylist = new ObjectArrayList<>(pArray);
        shuffle(objectarraylist, pRandom);
        return objectarraylist;
    }

    public static <T> List<T> shuffledCopy(ObjectArrayList<T> pList, RandomSource pRandom) {
        ObjectArrayList<T> objectarraylist = new ObjectArrayList<>(pList);
        shuffle(objectarraylist, pRandom);
        return objectarraylist;
    }

    public static <T> void shuffle(List<T> p_309952_, RandomSource pRandom) {
        int i = p_309952_.size();

        for (int j = i; j > 1; j--) {
            int k = pRandom.nextInt(j);
            p_309952_.set(j - 1, p_309952_.set(k, p_309952_.get(j - 1)));
        }
    }

    public static <T> CompletableFuture<T> blockUntilDone(Function<Executor, CompletableFuture<T>> pTask) {
        return blockUntilDone(pTask, CompletableFuture::isDone);
    }

    public static <T> T blockUntilDone(Function<Executor, T> pTask, Predicate<T> pDonePredicate) {
        BlockingQueue<Runnable> blockingqueue = new LinkedBlockingQueue<>();
        T t = pTask.apply(blockingqueue::add);

        while (!pDonePredicate.test(t)) {
            try {
                Runnable runnable = blockingqueue.poll(100L, TimeUnit.MILLISECONDS);
                if (runnable != null) {
                    runnable.run();
                }
            } catch (InterruptedException interruptedexception) {
                LOGGER.warn("Interrupted wait");
                break;
            }
        }

        int i = blockingqueue.size();
        if (i > 0) {
            LOGGER.warn("Tasks left in queue: {}", i);
        }

        return t;
    }

    public static <T> ToIntFunction<T> createIndexLookup(List<T> pList) {
        int i = pList.size();
        if (i < 8) {
            return pList::indexOf;
        } else {
            Object2IntMap<T> object2intmap = new Object2IntOpenHashMap<>(i);
            object2intmap.defaultReturnValue(-1);

            for (int j = 0; j < i; j++) {
                object2intmap.put(pList.get(j), j);
            }

            return object2intmap;
        }
    }

    public static <T> ToIntFunction<T> m_307438_(List<T> p_310693_) {
        int i = p_310693_.size();
        if (i < 8) {
            ReferenceList<T> referencelist = new ReferenceImmutableList<>(p_310693_);
            return referencelist::indexOf;
        } else {
            Reference2IntMap<T> reference2intmap = new Reference2IntOpenHashMap<>(i);
            reference2intmap.defaultReturnValue(-1);

            for (int j = 0; j < i; j++) {
                reference2intmap.put(p_310693_.get(j), j);
            }

            return reference2intmap;
        }
    }

    public static <A, B> Typed<B> m_306942_(Typed<A> p_309938_, Type<B> p_312439_, UnaryOperator<Dynamic<?>> p_312172_) {
        Dynamic<?> dynamic = (Dynamic<?>)p_309938_.write().getOrThrow();
        return m_306397_(p_312439_, p_312172_.apply(dynamic), true);
    }

    public static <T> Typed<T> m_305473_(Type<T> p_309502_, Dynamic<?> p_310749_) {
        return m_306397_(p_309502_, p_310749_, false);
    }

    public static <T> Typed<T> m_306397_(Type<T> p_309451_, Dynamic<?> p_312737_, boolean p_310890_) {
        DataResult<Typed<T>> dataresult = p_309451_.readTyped(p_312737_).map(Pair::getFirst);

        try {
            return p_310890_ ? dataresult.getPartialOrThrow(IllegalStateException::new) : dataresult.getOrThrow(IllegalStateException::new);
        } catch (IllegalStateException illegalstateexception) {
            CrashReport crashreport = CrashReport.forThrowable(illegalstateexception, "Reading type");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Info");
            crashreportcategory.setDetail("Data", p_312737_);
            crashreportcategory.setDetail("Type", p_309451_);
            throw new ReportedException(crashreport);
        }
    }

    public static <T> List<T> m_324319_(List<T> p_329243_, T p_329663_) {
        return ImmutableList.<T>builderWithExpectedSize(p_329243_.size() + 1).addAll(p_329243_).add(p_329663_).build();
    }

    public static <T> List<T> m_321242_(T p_330591_, List<T> p_336069_) {
        return ImmutableList.<T>builderWithExpectedSize(p_336069_.size() + 1).add(p_330591_).addAll(p_336069_).build();
    }

    public static <K, V> Map<K, V> m_321632_(Map<K, V> p_334319_, K p_335336_, V p_331863_) {
        return ImmutableMap.<K, V>builderWithExpectedSize(p_334319_.size() + 1).putAll(p_334319_).put(p_335336_, p_331863_).buildKeepingLast();
    }

    public static enum OS {
        LINUX("linux"),
        SOLARIS("solaris"),
        WINDOWS("windows") {
            @Override
            protected String[] getOpenUrlArguments(URL p_137662_) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", p_137662_.toString()};
            }
        },
        OSX("mac") {
            @Override
            protected String[] getOpenUrlArguments(URL p_137667_) {
                return new String[]{"open", p_137667_.toString()};
            }
        },
        UNKNOWN("unknown");

        private final String telemetryName;

        OS(final String pTelemetryName) {
            this.telemetryName = pTelemetryName;
        }

        public void openUrl(URL pUrl) {
            try {
                Process process = AccessController.doPrivileged((PrivilegedExceptionAction<Process>)(() -> Runtime.getRuntime().exec(this.getOpenUrlArguments(pUrl))));
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
            } catch (IOException | PrivilegedActionException privilegedactionexception) {
                Util.LOGGER.error("Couldn't open url '{}'", pUrl, privilegedactionexception);
            }
        }

        public void openUri(URI pUri) {
            try {
                this.openUrl(pUri.toURL());
            } catch (MalformedURLException malformedurlexception) {
                Util.LOGGER.error("Couldn't open uri '{}'", pUri, malformedurlexception);
            }
        }

        public void openFile(File pFile) {
            try {
                this.openUrl(pFile.toURI().toURL());
            } catch (MalformedURLException malformedurlexception) {
                Util.LOGGER.error("Couldn't open file '{}'", pFile, malformedurlexception);
            }
        }

        protected String[] getOpenUrlArguments(URL pUrl) {
            String s = pUrl.toString();
            if ("file".equals(pUrl.getProtocol())) {
                s = s.replace("file:", "file://");
            }

            return new String[]{"xdg-open", s};
        }

        public void openUri(String pUri) {
            try {
                this.openUrl(new URI(pUri).toURL());
            } catch (MalformedURLException | IllegalArgumentException | URISyntaxException urisyntaxexception) {
                Util.LOGGER.error("Couldn't open uri '{}'", pUri, urisyntaxexception);
            }
        }

        public String telemetryName() {
            return this.telemetryName;
        }
    }
}
